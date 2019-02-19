package co.raverapp.events.event

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.raverapp.base.viewholders.BottomFabSpacerViewHolder
import co.raverapp.base.viewholders.SubheaderViewHolder
import co.raverapp.base.viewholders.TwoLineTextOnlyViewHolder
import co.raverapp.android.data.events.Artist
import co.raverapp.android.data.events.Event
import co.raverapp.android.data.events.Venue
import co.raverapp.events.R
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.events_activity_event.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import javax.inject.Inject

class EventActivity : AppCompatActivity() {

    @Inject
    internal lateinit var disposables: CompositeDisposable

    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        val scope = createDependencyInjectionScope()

        super.onCreate(savedInstanceState)

        inject(scope)

        if (savedInstanceState == null) initFromExtras(intent.extras!!)
        else initFromSavedState(savedInstanceState)

        initView()
    }

    private fun createDependencyInjectionScope(): Scope {
        val scope = Toothpick.openScope(INJECT_SCOPE)
        scope.installModules(object : Module() {
            init {
                bind(CompositeDisposable::class.java).toProviderInstance { CompositeDisposable() }
            }
        })

        return scope
    }

    private fun inject(scope: Scope) {
        Toothpick.inject(this, scope)
    }

    private fun initFromExtras(extras: Bundle) {
        event = extras.getParcelable(EXTRA_EVENT)!!
    }

    private fun initFromSavedState(savedInstanceState: Bundle) {
        event = savedInstanceState.getParcelable(KEY_EVENT)!!
    }

    private fun initView() {
        setContentView(R.layout.events_activity_event)

        setSupportActionBar(events_activity_event_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initFab()
    }

    private fun initFab() {
        events_activity_event_fab.setOnClickListener {
            val intent = Intent(ACTION_VIEW, Uri.parse(event.websiteUrl))
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        initRecyclerView(event)
    }

    private fun initRecyclerView(event: Event) {
        val recyclerView = events_activity_event_recyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = InfoAdapter(event)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putParcelable(KEY_EVENT, event)
    }

    override fun onStop() {
        disposables.clear()

        super.onStop()
    }

    override fun onDestroy() {
        Toothpick.closeScope(INJECT_SCOPE)

        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    companion object {
        private val INJECT_SCOPE = EventActivity::class.java

        private const val EXTRA_EVENT = "extra_event"
        private const val KEY_EVENT = "key_event"

        fun getStartIntent(context: Context, event: Event): Intent {
            val intent = Intent(context, EventActivity::class.java)
            intent.putExtra(EXTRA_EVENT, event)

            return intent
        }

    }

}

private class InfoAdapter(private val event: Event) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val picasso = Picasso.get()

    //region positions
    private val posArtistSubheader = 2
    private val posArtistsStart = posArtistSubheader + 1

    // the end of the artists, including this position
    private val posArtistsEnd = posArtistsStart + event.artists.size - 1
    private val posVenueSubheader = posArtistsEnd + 1
    private val posVenue = posVenueSubheader + 1
    //endregion

    override fun getItemCount(): Int {
        // Banner = 1 view
        // Header = 1 view
        // Artists Header = 1 view
        // Artists Views = num artists
        // Venue Header = 1 view
        // Venue = 1 view
        // spacer for FAB = 1 view

        return 6 + event.artists.size
    }

    //region view types
    private val viewTypeBanner = 0
    private val viewTypeHeader = 1
    private val viewTypeSubheaderArtists = 2
    private val viewTypeArtist = 3
    private val viewTypeSubheaderVenue = 4
    private val viewTypeVenue = 5
    private val viewTypeFabSpacer = 6
    //endregion

    override fun getItemViewType(position: Int) =
        when (position) {
            0 -> viewTypeBanner
            1 -> viewTypeHeader
            2 -> viewTypeSubheaderArtists
            in posArtistsStart..posArtistsEnd -> viewTypeArtist
            posVenueSubheader -> viewTypeSubheaderVenue
            posVenue -> viewTypeVenue
            else -> viewTypeFabSpacer
        }

    private class BannerViewHolder(
        itemView: View,
        private val picasso: Picasso
    ) : RecyclerView.ViewHolder(itemView) {
        val banner: ImageView = itemView.findViewById(R.id.events_activity_event_item_banner_img)

        fun bind(event: Event) {
            val bannerVisibility =
                if (event.bannerUrl == null) View.GONE
                else View.VISIBLE
            banner.visibility = bannerVisibility

            if (event.bannerUrl != null) {
                picasso.load(event.bannerUrl).into(banner)
            }
        }
    }

    private class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.events_activity_event_item_header_name)
        val dateTime: TextView = itemView.findViewById(R.id.events_activity_event_item_header_datetime)

        @SuppressLint("SetTextI18n")
        fun bind(event: Event) {
            title.text = event.title

            val dateParser = DateTimeFormatter.ISO_LOCAL_DATE
            val startDateFormatter = DateTimeFormatter.ofPattern("MMM d")
            var dateString = LocalDate.parse(event.dates[0], dateParser).format(startDateFormatter)
            if (event.isFestival) {
                val endDateFormatter = DateTimeFormatter.ofPattern("d")
                val endDateString = LocalDate.parse(event.dates.last(), dateParser).format(endDateFormatter)
                dateString = "$dateString - $endDateString"
            }

            val timeParser = DateTimeFormatter.ISO_LOCAL_TIME
            val timeFormatter = DateTimeFormatter.ofPattern("hh a")
            val timeString = LocalTime.parse(event.startTime, timeParser).format(timeFormatter)
            dateTime.text = "$dateString @ $timeString"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        return when (viewType) {
            viewTypeBanner -> {
                val view = inflater.inflate(R.layout.events_activity_event_item_banner, parent, false)
                BannerViewHolder(view, picasso)
            }
            viewTypeHeader -> {
                val view = inflater.inflate(R.layout.events_activity_event_item_header, parent, false)
                HeaderViewHolder(view)
            }
            viewTypeSubheaderArtists -> SubheaderViewHolder(context, parent)
            viewTypeArtist -> TwoLineTextOnlyViewHolder(context, parent)
            viewTypeSubheaderVenue -> SubheaderViewHolder(context, parent)
            viewTypeVenue -> TwoLineTextOnlyViewHolder(context, parent)
            viewTypeFabSpacer -> BottomFabSpacerViewHolder(context, parent)
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            viewTypeBanner -> (holder as BannerViewHolder).bind(event)
            viewTypeHeader -> (holder as HeaderViewHolder).bind(event)
            viewTypeSubheaderArtists -> {
                (holder as SubheaderViewHolder).title.setText(R.string.events_activity_event_subhead_artists)
            }
            viewTypeArtist -> {
                bindArtist(holder as TwoLineTextOnlyViewHolder, event.artists[position - posArtistsStart])
            }
            viewTypeSubheaderVenue -> {
                (holder as SubheaderViewHolder).title.setText(R.string.events_activity_event_subhead_venue)
            }
            viewTypeVenue -> {
                bindVenue(holder as TwoLineTextOnlyViewHolder, event.venue)
            }
        }
    }

    private fun bindArtist(holder: TwoLineTextOnlyViewHolder, artist: Artist) {
        holder.title.text = artist.name
        holder.subtitle.text = artist.genres.joinToString(separator = ", ")
    }

    @SuppressLint("SetTextI18n")
    private fun bindVenue(holder: TwoLineTextOnlyViewHolder, venue: Venue) {
        holder.title.text = venue.name
        holder.subtitle.text = venue.city + ", " + event.venue.state
    }

}
