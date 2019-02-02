package co.raverapp.Raver.events.events

import android.os.Bundle
import android.support.annotation.ColorInt
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.raverapp.Raver.events.R
import co.raverapp.Raver.events.event.EventActivity
import co.raverapp.android.data.events.Event
import co.raverapp.android.data.events.IEventRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.events_activity_events.*
import org.threeten.bp.format.DateTimeFormatter
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
        initEventsList(events)
    }

    private fun initToolbar() {
        setSupportActionBar(launches_activity_launches_toolbar)
    }

    private fun initEventsList(events: List<Event>?) {
        val viewManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter(events) {
            uiHandler.eventClicked(it)
        }
        launches_activity_launches_recyclerview.apply {
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

                eventAdapter.events = events
            }, {
                launches_activity_launches_error.text = "Error"
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

    var events: List<Event>? = events
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    class EventViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val labelContainer: ViewGroup = view.findViewById(R.id.events_item_event_label_container)
        val label: TextView = view.findViewById(R.id.events_item_event_label)
        val title: TextView = view.findViewById(R.id.events_item_event_title)
        val date: TextView = view.findViewById(R.id.events_item_event_date)
        val city: TextView = view.findViewById(R.id.events_item_event_city)
        val headlinerName: TextView = view.findViewById(R.id.events_item_event_headliner_name)
        val headlinerGernes: TextView = view.findViewById(R.id.events_item_event_headliner_genres)
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

        bindLabel(holder, event)

        holder.title.text = event.title

        val formatter = DateTimeFormatter.ofPattern("MMM d")
        holder.date.text = event.dateTime.format(formatter)

        holder.city.text = event.venue.city

        val headliner = event.artists[0]
        holder.headlinerName.text = headliner.name
        holder.headlinerGernes.text = headliner.genres.joinToString(separator = ", ")
    }

    private fun bindLabel(holder: EventViewHolder, event: Event) {
        val indexPrimaryDark = 0
        val indexPrimaryLight = 1
        val indexTextPrimary = 2
        val indexTextPrimaryInverse = 3
        val attrs = intArrayOf(
            R.attr.colorPrimaryDark,
            R.attr.colorPrimaryLight,
            android.R.attr.textColorPrimary,
            android.R.attr.textColorPrimaryInverse
        )
        val themeAttrs = holder.itemView.context.obtainStyledAttributes(R.style.AppTheme, attrs)

        @StringRes val labelResId: Int
        @ColorInt val labelTextColor: Int
        @ColorInt val labelBackgroundColor: Int
        if (event.isFestival) {
            labelResId = R.string.events_label_event_massive
            labelTextColor = themeAttrs.getColor(indexTextPrimaryInverse, 0)
            labelBackgroundColor = themeAttrs.getColor(indexPrimaryDark, 0)
        } else {
            labelResId = R.string.events_label_event_small
            labelTextColor = themeAttrs.getColor(indexPrimaryLight, 0)
            labelBackgroundColor = themeAttrs.getColor(indexTextPrimary, 0)
        }
        holder.label.setText(labelResId)
        holder.label.setTextColor(labelTextColor)
        holder.labelContainer.setBackgroundColor(labelBackgroundColor)

        themeAttrs.recycle()
    }

    override fun getItemCount() = events?.size ?: 0

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        holder.itemView.setOnClickListener(null)
    }

}
