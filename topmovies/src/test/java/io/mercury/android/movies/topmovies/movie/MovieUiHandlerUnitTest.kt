package io.mercury.android.movies.topmovies.movie

import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test

class MovieUiHandlerUnitTest {

    private lateinit var uiHandler: MovieUiHandler
    private lateinit var actionSubscriber: TestSubscriber<Action>

    @Before
    fun SetUp() {
        uiHandler = MovieUiHandler()

        actionSubscriber = TestSubscriber()
    }

    @Test
    fun BackClicked_BackActionEmitted() {
        uiHandler.actions.subscribe(actionSubscriber)

        uiHandler.backClicked()

        actionSubscriber.assertValue { it is NavigateBack }
    }

}
