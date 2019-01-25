package io.mercury.android.movies.topmovies.topmovies

import com.dpanger.android.launches.data.launches.ILaunchRepository
import com.dpanger.android.launches.data.launches.PagedLaunchSummary
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test

class LaunchesViewModelUnitTest {

    private lateinit var repository: ILaunchRepository
    private lateinit var viewModel: LaunchesViewModel

    @Before
    fun SetUp() {
        repository = mock()
        viewModel = LaunchesViewModel(repository)
    }

    @Test
    fun GetUpcomingLaunches_RepositoryUsed() {
        val offset = 0

        val launchesFlowable = Flowable.empty<PagedLaunchSummary>()
        whenever(repository.getUpcomingLaunches(offset)).thenReturn(launchesFlowable)

        viewModel.topMovies

        verify(repository).getUpcomingLaunches(offset)
    }

}
