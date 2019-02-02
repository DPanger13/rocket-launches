package co.raverapp.Raver.events.events

import co.raverapp.android.data.events.Event
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

internal class LaunchesUiHandler @Inject constructor() {
    private val _actions = PublishSubject.create<Action>()
    val actions: Flowable<Action> = _actions.toFlowable(BackpressureStrategy.BUFFER)

    fun eventClicked(event: Event) {
        _actions.onNext(ShowEvent(event))
    }

}

sealed class Action

data class ShowEvent(val event: Event) : Action()
