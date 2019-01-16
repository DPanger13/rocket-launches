package io.mercury.android.movies.topmovies.topmovies

import io.mercury.android.movies.data.movies.IMovieRepository
import io.mercury.android.movies.data.movies.TopMovie
import io.reactivex.Flowable
import javax.inject.Inject

internal class TopMoviesViewModel @Inject constructor(private val repository: IMovieRepository) {

    val topMovies: Flowable<List<TopMovie>> = repository.getTopMovies()

}
