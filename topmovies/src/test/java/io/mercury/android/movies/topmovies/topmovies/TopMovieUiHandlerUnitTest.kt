package io.mercury.android.movies.topmovies.topmovies

import io.mercury.android.movies.data.movies.TopMovie
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test

internal class TopMovieUiHandlerUnitTest {

    private lateinit var uiHandler: TopMovieUiHandler
    private lateinit var actionSubscriber: TestSubscriber<Action>

    @Before
    fun SetUp() {
        uiHandler = TopMovieUiHandler()

        actionSubscriber = TestSubscriber()
    }

    @Test
    fun HandleMovieClicked_LaunchActionEmitted() {
        val movieId = "tt11000"
        val topMovie = TopMovie(
            movieId,
            "title",
            1900,
            null,
            1,
            TopMovie.ExtendedImdbInfo(movieId, 2.4, 27, null)
        )
        uiHandler.actions.subscribe(actionSubscriber)

        uiHandler.movieClicked(topMovie)

        actionSubscriber.assertValue { it is ShowMovie }
    }

}
