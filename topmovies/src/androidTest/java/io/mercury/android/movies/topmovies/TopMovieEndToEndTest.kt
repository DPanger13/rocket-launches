package io.mercury.android.movies.topmovies

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mercury.android.movies.topmovies.movie.MovieActivity
import io.mercury.android.movies.topmovies.topmovies.TopMoviesActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopMovieEndToEndTest {

    @get:Rule
    val intentsRule = IntentsTestRule(TopMoviesActivity::class.java)

    @Test
    fun LaunchTopMovies_ClickMovie_MovieDetailsShow() {
        onView(withId(R.id.topmovies_activity_top_movies_recyclerview))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // make sure the movie screen launches
        intended(hasComponent(MovieActivity::class.java.name))

        // double-check that the details screen is showing correctly
        val director = "Francis Ford Coppola"
        onView(withText(director))
    }

}
