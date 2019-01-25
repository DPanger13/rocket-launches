package io.mercury.android.movies.topmovies.topmovies

import com.dpanger.android.launches.data.launches.LaunchSummary
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

internal class LaunchesUiHandler @Inject constructor() {
    private val _actions = PublishSubject.create<Action>()
    val actions: Flowable<Action> = _actions.toFlowable(BackpressureStrategy.BUFFER)

    fun movieClicked(launch: LaunchSummary) {
        _actions.onNext(ShowLaunch(launch.id))
    }

}

sealed class Action

data class ShowLaunch(val id: Int) : Action()
