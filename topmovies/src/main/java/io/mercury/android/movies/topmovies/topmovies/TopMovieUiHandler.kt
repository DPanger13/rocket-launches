package io.mercury.android.movies.topmovies.topmovies

import io.mercury.android.movies.data.movies.TopMovie
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

internal class TopMovieUiHandler @Inject constructor() {
    private val _actions = PublishSubject.create<Action>()
    val actions: Flowable<Action> = _actions.toFlowable(BackpressureStrategy.BUFFER)

    fun movieClicked(movie: TopMovie) {
        _actions.onNext(ShowMovie(movie.id))
    }

}

sealed class Action

data class ShowMovie(val movieId: String) : Action()
