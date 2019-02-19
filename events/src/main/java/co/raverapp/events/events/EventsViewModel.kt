package co.raverapp.events.events

import co.raverapp.android.data.events.Event
import co.raverapp.android.data.events.IEventRepository
import io.reactivex.Flowable
import javax.inject.Inject

internal class EventsViewModel @Inject constructor(private val repository: IEventRepository) {

    val topMovies: Flowable<List<Event>>
        get() = repository.getEvents().toFlowable()

}
