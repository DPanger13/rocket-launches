package co.raverapp.Raver.events.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.raverapp.Raver.events.R
import co.raverapp.Raver.events.event.EventActivity
import co.raverapp.android.data.events.Event
import co.raverapp.android.data.events.IEventRepository
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.events_activity_events.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import javax.inject.Inject

class EventsActivity : AppCompatActivity() {

    @Inject
    internal lateinit var disposables: CompositeDisposable

    @Inject
    internal lateinit var uiHandler: LaunchesUiHandler

    @Inject
    internal lateinit var viewModel: EventsViewModel

    private lateinit var eventAdapter: EventAdapter
    private var events: List<Event>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val scope = createDependencyInjectionScope()

        super.onCreate(savedInstanceState)

        inject(scope)

        if (savedInstanceState != null) initFromSavedState(savedInstanceState)

        initView()
    }

    private fun createDependencyInjectionScope(): Scope {
        val scope = Toothpick.openScope(INJECT_SCOPE)
        scope.installModules(object: Module() {
            init {
                bind(CompositeDisposable::class.java).toProviderInstance { CompositeDisposable() }
                bind(IEventRepository::class.java).toProviderInstance { IEventRepository.create() }
            }
        })

        return scope
    }

    private fun inject(scope: Scope) {
        Toothpick.inject(this, scope)
    }

    private fun initFromSavedState(savedInstanceState: Bundle) {
        events = savedInstanceState.getParcelableArrayList(KEY_EVENTS)
    }

    private fun initView() {
        setContentView(R.layout.events_activity_events)
        initToolbar()
        initRefreshLayout()
        initEventsList(events)
    }

    private fun initToolbar() {
        setSupportActionBar(events_activity_events_toolbar)
    }

    private fun initRefreshLayout() {
        val refreshLayout = events_activity_events_refreshlayout
        refreshLayout.setOnRefreshListener {
            fetchMovies()
        }
    }

    private fun initEventsList(events: List<Event>?) {
        val viewManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter(events) {
            uiHandler.eventClicked(it)
        }
        events_activity_events_recyclerview.apply {
            layoutManager = viewManager
            adapter = eventAdapter
        }
    }

    override fun onStart() {
        super.onStart()

        observeUiHandler()

        if (events == null) {
            fetchMovies()
        }
    }

    private fun observeUiHandler() {
        val disposable = uiHandler.actions.subscribe {
            when (it) {
                is ShowEvent -> {
                    val intent = EventActivity.getStartIntent(this, it.event)
                    startActivity(intent)
                }
            }
        }
        disposables.add(disposable)
    }

    private fun fetchMovies() {
        val disposable = viewModel.topMovies
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                events = it

                val refreshLayout = events_activity_events_refreshlayout
                if (refreshLayout.isRefreshing) refreshLayout.isRefreshing = false

                eventAdapter.events = events
            }, {
                Timber.e(it)

                events_activity_events_error.text = "Error"
            })
        disposables.add(disposable)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        if (events != null) {
            outState?.putParcelableArrayList(KEY_EVENTS, ArrayList(events!!.toMutableList()))
        }
    }

    override fun onStop() {
        disposables.clear()

        super.onStop()
    }

    override fun onDestroy() {
        Toothpick.closeScope(INJECT_SCOPE)

        super.onDestroy()
    }

    private companion object {
        private val INJECT_SCOPE = EventsActivity::class.java

        private const val KEY_EVENTS = "key_events"
    }

}

class EventAdapter(
    events: List<Event>?,
    private val onEventClicked: (Event) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val picasso = Picasso.get()

    var events: List<Event>? = events
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    class EventViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val banner: ImageView = view.findViewById(R.id.events_item_event_banner)
        val label: TextView = view.findViewById(R.id.events_item_event_label)
        val title: TextView = view.findViewById(R.id.events_item_event_title)
        val date: TextView = view.findViewById(R.id.events_item_event_date)
        val city: TextView = view.findViewById(R.id.events_item_event_city)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.events_item_event, parent, false) as ViewGroup

        return EventViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = events?.get(position) ?: return

        bindEventHolder(holder as EventViewHolder, event)
    }

    private fun bindEventHolder(holder: EventViewHolder, event: Event) {
        holder.itemView.setOnClickListener { onEventClicked.invoke(event) }

        val bannerVisibility =
            if (event.bannerUrl == null) View.GONE
            else View.VISIBLE
        holder.banner.visibility = bannerVisibility

        if (event.bannerUrl != null) {
            picasso.load(event.bannerUrl).into(holder.banner)
        }

        @StringRes val labelResId = if (event.isFestival) {
            R.string.events_label_event_massive
        } else {
            R.string.events_label_event_small
        }
        holder.label.setText(labelResId)

        holder.title.text = event.title

        val dateParser = DateTimeFormatter.ISO_LOCAL_DATE
        val dateFormatter = DateTimeFormatter.ofPattern("MMM d")
        var dateString = LocalDate.parse(event.dates[0], dateParser).format(dateFormatter)
        if (event.isFestival) {
            val endDateFormatter = DateTimeFormatter.ofPattern("d")
            val endDateString = LocalDate.parse(event.dates.last(), dateParser).format(endDateFormatter)
            dateString  = "$dateString - $endDateString"
        }
        holder.date.text = dateString

        holder.city.text = event.venue.city
    }

    override fun getItemCount() = events?.size ?: 0

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        holder.itemView.setOnClickListener(null)
    }

}
