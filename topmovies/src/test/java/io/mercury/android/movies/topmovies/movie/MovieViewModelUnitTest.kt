package io.mercury.android.movies.topmovies.movie

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.mercury.android.movies.data.movies.DetailedMovieInfo
import io.mercury.android.movies.data.movies.IMovieRepository
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test

class MovieViewModelUnitTest {

    private lateinit var repository: IMovieRepository
    private lateinit var viewModel: MovieViewModel

    @Before
    fun SetUp() {
        repository = mock()
        viewModel = MovieViewModel(repository)
    }

    @Test
    fun GetMovie_RepositoryUsed() {
        val movieId = "rr222003"

        val movieFlowable = Flowable.empty<DetailedMovieInfo>()
        whenever(repository.getMovieInfo(movieId)).thenReturn(movieFlowable)

        viewModel.getMovieInfo(movieId)

        verify(repository).getMovieInfo(movieId)
    }

}
