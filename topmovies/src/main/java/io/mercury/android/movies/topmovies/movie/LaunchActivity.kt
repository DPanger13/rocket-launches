package io.mercury.android.movies.topmovies.movie

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dpanger.android.launches.data.launches.Launch
import com.dpanger.android.launches.data.launches.LaunchModule
import io.mercury.android.movies.topmovies.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.launches_activity_launch.*
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import javax.inject.Inject

class LaunchActivity : AppCompatActivity() {

    @Inject
    internal lateinit var disposables: CompositeDisposable

    @Inject
    internal lateinit var viewModel: LaunchViewModel

    private var launchId: Int = -1
    private var launch: Launch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val scope = createDependencyInjectionScope()

        super.onCreate(savedInstanceState)

        inject(scope)

        if (savedInstanceState == null) initFromExtras(intent.extras!!)
        else initFromSavedState(savedInstanceState)

        setContentView(R.layout.launches_activity_launch)
    }

    private fun createDependencyInjectionScope(): Scope {
        val scope = Toothpick.openScope(INJECT_SCOPE)
        scope.installModules(LaunchModule(), object : Module() {
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
        launchId = extras.getInt(EXTRA_ID_LAUNCH)
    }

    private fun initFromSavedState(savedInstanceState: Bundle) {
        launchId = savedInstanceState.getInt(KEY_ID_LAUNCH)

        launch = savedInstanceState.getParcelable(KEY_LAUNCH)
    }

    override fun onStart() {
        super.onStart()

        if (launch == null) {
            fetchMovie()
        } else {
            updateUiWithMovieInfo(launch!!)
        }
    }

    private fun fetchMovie() {
        val disposable = viewModel.getLaunch(launchId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                launch = it

                updateUiWithMovieInfo(it)
            }, {
                launches_activity_launch_error.setText(R.string.error)
            })
        disposables.add(disposable)
    }

    private fun updateUiWithMovieInfo(launch: Launch) {
        launches_activity_launch_datetime.text = launch.dateTime.toString()
        launches_activity_launch_id.text = launch.id.toString()
        launches_activity_launch_location.text = launch.location.name
        launches_activity_launch_missions.text = launch.missions.toString()
        launches_activity_launch_name.text = launch.name
        launches_activity_launch_rocket.text = launch.rocket.name
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putInt(KEY_ID_LAUNCH, launchId)

        if (launch != null) outState?.putParcelable(KEY_LAUNCH, launch)
    }

    override fun onStop() {
        disposables.clear()

        super.onStop()
    }

    override fun onDestroy() {
        Toothpick.closeScope(INJECT_SCOPE)

        super.onDestroy()
    }

    companion object {
        private val INJECT_SCOPE = LaunchActivity::class.java

        private const val EXTRA_ID_LAUNCH = "extra_id_launch"
        private const val KEY_ID_LAUNCH = "key_id_launch"
        private const val KEY_LAUNCH = "key_launch"

        fun getStartIntent(context: Context, launchId: Int): Intent {
            val intent = Intent(context, LaunchActivity::class.java)
            intent.putExtra(EXTRA_ID_LAUNCH, launchId)

            return intent
        }

    }

}
