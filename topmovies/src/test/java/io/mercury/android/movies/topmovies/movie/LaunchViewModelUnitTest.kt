package io.mercury.android.movies.topmovies.movie

import com.dpanger.android.launches.data.launches.ILaunchRepository
import com.dpanger.android.launches.data.launches.Launch
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test

class LaunchViewModelUnitTest {

    private lateinit var repository: ILaunchRepository
    private lateinit var viewModel: LaunchViewModel

    @Before
    fun SetUp() {
        repository = mock()
        viewModel = LaunchViewModel(repository)
    }

    @Test
    fun GetLaunch_RepositoryUsed() {
        val launchId = 222003

        val launchFlowable = Flowable.empty<Launch>()
        whenever(repository.getLaunch(launchId)).thenReturn(launchFlowable)

        viewModel.getLaunch(launchId)

        verify(repository).getLaunch(launchId)
    }

}
