package com.dpanger.android.launches.launches.launches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dpanger.android.launches.data.launches.LaunchModule
import com.dpanger.android.launches.data.launches.LaunchSummary
import com.dpanger.android.launches.data.launches.PagedLaunchSummary
import com.dpanger.android.launches.launches.R
import com.dpanger.android.launches.launches.launch.LaunchActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.launches_activity_launches.*
import org.threeten.bp.format.DateTimeFormatter
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import javax.inject.Inject

class LaunchesActivity : AppCompatActivity() {

    @Inject
    internal lateinit var disposables: CompositeDisposable

    @Inject
    internal lateinit var uiHandler: LaunchesUiHandler

    @Inject
    internal lateinit var viewModel: LaunchesViewModel

    private lateinit var launchAdapter: LaunchAdapter
    private var pagedLaunchSummary: PagedLaunchSummary? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val scope = createDependencyInjectionScope()

        super.onCreate(savedInstanceState)

        inject(scope)

        if (savedInstanceState != null) initFromSavedState(savedInstanceState)

        initView()
    }

    private fun createDependencyInjectionScope(): Scope {
        val scope = Toothpick.openScope(INJECT_SCOPE)
        scope.installModules(LaunchModule(), object: Module() {
            init {
                bind(CompositeDisposable::class.java).toProviderInstance { CompositeDisposable() }
            }
        })

        return scope
    }

    private fun inject(scope: Scope) {
        Toothpick.inject(this, scope)
    }

    private fun initFromSavedState(savedInstanceState: Bundle) {
        pagedLaunchSummary = savedInstanceState.getParcelable(KEY_LAUNCHES)
    }

    private fun initView() {
        setContentView(R.layout.launches_activity_launches)
        initToolbar()
        initLaunchList(pagedLaunchSummary)
    }

    private fun initToolbar() {
        setSupportActionBar(launches_activity_launches_toolbar)
    }

    private fun initLaunchList(pagedLaunchSummary: PagedLaunchSummary?) {
        val viewManager = LinearLayoutManager(this)
        launchAdapter = LaunchAdapter(pagedLaunchSummary) {
            uiHandler.movieClicked(it)
        }
        launches_activity_launches_recyclerview.apply {
            layoutManager = viewManager
            adapter = launchAdapter
        }
    }

    override fun onStart() {
        super.onStart()

        observeUiHandler()

        if (pagedLaunchSummary == null) {
            fetchMovies()
        }
    }

    private fun observeUiHandler() {
        val disposable = uiHandler.actions.subscribe {
            when (it) {
                is ShowLaunch -> {
                    val intent = LaunchActivity.getStartIntent(this, it.id)
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
                pagedLaunchSummary = it

                launchAdapter.pagedLaunchSummary = pagedLaunchSummary
            }, {
                launches_activity_launches_error.text = "Error"
            })
        disposables.add(disposable)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        if (pagedLaunchSummary != null) {
            outState?.putParcelable(KEY_LAUNCHES, pagedLaunchSummary)
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
        private val INJECT_SCOPE = LaunchesActivity::class.java

        private const val KEY_LAUNCHES = "key_launches"
    }

}

class LaunchAdapter(
    pagedLaunchSummary: PagedLaunchSummary?,
    private val onLaunchClicked: (LaunchSummary) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var pagedLaunchSummary: PagedLaunchSummary? = pagedLaunchSummary
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    class LaunchViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.launches_item_launch_name)
        val dateTime: TextView = view.findViewById(R.id.launches_item_launch_datetime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.launches_item_launch, parent, false) as ViewGroup

        return LaunchViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val launch = pagedLaunchSummary?.launches?.get(position) ?: return

        val launchHolder = holder as LaunchViewHolder
        launchHolder.name.text = launch.name

        val formatter = DateTimeFormatter.ofPattern("MMM d")
        launchHolder.dateTime.text = launch.dateTime.format(formatter)

        launchHolder.itemView.setOnClickListener { onLaunchClicked.invoke(launch) }
    }

    override fun getItemCount() = pagedLaunchSummary?.launches?.size ?: 0

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        holder.itemView.setOnClickListener(null)
    }

}
