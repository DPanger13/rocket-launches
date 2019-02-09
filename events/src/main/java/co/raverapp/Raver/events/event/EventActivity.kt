package co.raverapp.Raver.events.event

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import co.raverapp.Raver.events.R
import co.raverapp.android.data.events.Artist
import co.raverapp.android.data.events.Event
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.events_activity_event.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetTime
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

        initHeaders()
    }

    private val flipVerticallyMatrix by lazy {
        val matrix = Matrix()
        matrix.postRotate(180f)

        matrix
    }

    private fun initHeaders() {
        initHeaderClickListener(
            events_activity_event_artists_header_label,
            events_activity_event_artists_header_icon,
            events_activity_event_artists_container
        )

        initHeaderClickListener(
            events_activity_event_venue_header_label,
            events_activity_event_venue_header_icon,
            events_activity_event_venue_container
        )
    }

    private fun initHeaderClickListener(headerContainer: ViewGroup, headerIcon: ImageView, infoContainer: ViewGroup) {
        headerContainer.setOnClickListener {
            headerIcon.scaleType = ImageView.ScaleType.MATRIX
            headerIcon.imageMatrix = flipVerticallyMatrix

            val infoVisibility = infoContainer.visibility
            if (infoVisibility == View.VISIBLE) infoContainer.visibility = View.GONE
            else infoContainer.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()

        updateUiWithEventInfo(event)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUiWithEventInfo(event: Event) {
        val bannerVisibility =
            if (event.banner == null) View.GONE
            else View.VISIBLE
        events_activity_event_banner.visibility = bannerVisibility

        if (event.banner != null) Picasso.get().load(event.banner.toString()).into(events_activity_event_banner)

        events_activity_event_name.text = event.title

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
        events_activity_event_datetime.text = "$dateString @ $timeString"

        events_activity_event_venue_name.text = event.venue.name
        events_activity_event_venue_location.text = event.venue.city + ", " + event.venue.state

        addArtistViews(event.artists)
    }

    private fun addArtistViews(artists: List<Artist>) {
        val container = events_activity_event_artists_container
        container.removeAllViews()

        val inflater = LayoutInflater.from(this)
        for (artist in artists) {
            val artistView = inflater.inflate(R.layout.events_item_artist, container, false)

            artistView.findViewById<TextView>(R.id.events_item_artist_name).text = artist.name
            artistView.findViewById<TextView>(R.id.events_item_artist_genres).text =
                    artist.genres.joinToString(separator = ", ")

            container.addView(artistView)
        }
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
