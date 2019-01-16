package io.mercury.android.movies.topmovies.topmovies

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.mercury.android.movies.data.movies.IMovieRepository
import io.mercury.android.movies.data.movies.TopMovie
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test

class TopMoviesViewModelUnitTest {

    private lateinit var repository: IMovieRepository
    private lateinit var viewModel: TopMoviesViewModel

    @Before
    fun SetUp() {
        repository = mock()
        viewModel = TopMoviesViewModel(repository)
    }

    @Test
    fun GetTopMovies_RepositoryUsed() {
        val topMovieFlowable = Flowable.empty<List<TopMovie>>()
        whenever(repository.getTopMovies()).thenReturn(topMovieFlowable)

        viewModel.topMovies

        verify(repository).getTopMovies()
    }

}
