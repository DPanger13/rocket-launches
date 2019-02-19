package co.raverapp.events.events

import co.raverapp.android.data.events.Event
import co.raverapp.android.data.events.IEventRepository
import co.raverapp.events.events.EventsViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class EventsViewModelUnitTest {

    private lateinit var repository: IEventRepository
    private lateinit var viewModel: EventsViewModel

    @Before
    fun SetUp() {
        repository = mock()
        viewModel = EventsViewModel(repository)
    }

    @Test
    fun GetUpcomingLaunches_RepositoryUsed() {
        val launchesFlowable = Single.error<List<Event>>(Exception())
        whenever(repository.getEvents()).thenReturn(launchesFlowable)

        viewModel.topMovies

        verify(repository).getEvents()
    }

}
