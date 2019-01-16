package io.mercury.android.movies.topmovies.movie

import io.mercury.android.movies.data.movies.DetailedMovieInfo
import io.mercury.android.movies.data.movies.IMovieRepository
import io.reactivex.Flowable
import javax.inject.Inject

internal class MovieViewModel @Inject constructor(private val repository: IMovieRepository) {

    fun getMovieInfo(movieId: String): Flowable<DetailedMovieInfo> = repository.getMovieInfo(movieId)

}
