package io.mercury.android.movies.topmovies.movie

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

internal class LaunchUiHandler @Inject constructor() {

    private val _actions = PublishSubject.create<Action>()
    val actions: Flowable<Action> = _actions.toFlowable(BackpressureStrategy.BUFFER)

    fun backClicked(): Unit = _actions.onNext(NavigateBack)

}

sealed class Action

/**
 * The user should be navigated backward according to Android navigation standards.
 */
object NavigateBack : Action()
