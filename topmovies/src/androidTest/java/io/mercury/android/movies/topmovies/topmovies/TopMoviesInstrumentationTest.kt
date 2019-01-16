package io.mercury.android.movies.topmovies.topmovies

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mercury.android.movies.data.movies.DetailedMovieInfo
import io.mercury.android.movies.data.movies.IMovieRepository
import io.mercury.android.movies.data.movies.TopMovie
import io.mercury.android.movies.topmovies.R
import io.mercury.android.movies.topmovies.movie.MovieActivity
import io.reactivex.Flowable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import toothpick.Toothpick
import toothpick.config.Module
import java.net.URI

@RunWith(AndroidJUnit4::class)
class TopMoviesInstrumentationTest {

    @get:Rule
    val intentsRule = IntentsTestRule(TopMoviesActivity::class.java, false, false)

    @Test
    fun Init_SuccessfullyLoadedData_TopMoviesShow() {
        val id = "tt0001"
        val title = "title"
        val year = 2001
        val poster = URI("example.com")
        val rank = 1
        val rating = 3.2
        val votes = 32984
        val imdbPage = URI("imdb.com")
        val imdbInfo = TopMovie.ExtendedImdbInfo(id, rating, votes, imdbPage)

        Toothpick.openScope(TopMoviesActivity::class.java).installTestModules(object : Module() {
            init {
                bind(IMovieRepository::class.java).toProviderInstance {
                    object : IMovieRepository {
                        override fun getMovieInfo(imdbId: String): Flowable<DetailedMovieInfo> {
                            throw UnsupportedOperationException("This method shouldn't be called during test.")
                        }

                        override fun getTopMovies(): Flowable<List<TopMovie>> {
                            val topMovie = TopMovie(
                                id,
                                title,
                                year,
                                poster,
                                rank,
                                imdbInfo
                            )

                            return Flowable.just(listOf(topMovie))
                        }
                    }
                }
            }
        })

        intentsRule.launchActivity(null)

        //check that all pieces of info are in the view tree
        onView(withText(id))
        onView(withText(title))
        onView(withText(year.toString()))
        onView(withText(poster.toString()))
        onView(withText(rank.toString()))
        onView(withText(rating.toString()))
        onView(withText(votes.toString()))
        onView(withText(imdbPage.toString()))
        onView(withText(imdbPage.toString()))
    }

    @Test
    fun Init_LoadingDataFailed_ErrorShows() {
        Toothpick.openScope(TopMoviesActivity::class.java).installTestModules(object : Module() {
            init {
                bind(IMovieRepository::class.java).toProviderInstance {
                    object : IMovieRepository {
                        override fun getMovieInfo(imdbId: String): Flowable<DetailedMovieInfo> {
                            throw UnsupportedOperationException("This method shouldn't be called during test.")
                        }

                        override fun getTopMovies(): Flowable<List<TopMovie>> {
                            return Flowable.error(Exception())
                        }
                    }
                }
            }
        })

        intentsRule.launchActivity(null)

        onView(withText("Error")).check(matches(isDisplayed()))
    }

    @Test
    fun MovieClicked_DetailScreenShowns() {
        val id = "tt0001"
        val title = "title"
        val year = 2001
        val poster = URI("example.com")
        val rank = 1
        val rating = 3.2
        val votes = 32984
        val imdbPage = URI("imdb.com")
        val imdbInfo = TopMovie.ExtendedImdbInfo(id, rating, votes, imdbPage)
        Toothpick.openScope(TopMoviesActivity::class.java).installTestModules(object : Module() {
            init {
                bind(IMovieRepository::class.java).toProviderInstance {
                    object : IMovieRepository {
                        override fun getMovieInfo(imdbId: String): Flowable<DetailedMovieInfo> {
                            throw UnsupportedOperationException("This method shouldn't be called during test.")
                        }

                        override fun getTopMovies(): Flowable<List<TopMovie>> {
                            val topMovie = TopMovie(
                                id,
                                title,
                                year,
                                poster,
                                rank,
                                imdbInfo
                            )

                            return Flowable.just(listOf(topMovie))
                        }
                    }
                }
            }
        })

        Toothpick.openScope(MovieActivity::class.java).installTestModules(object : Module() {
            init {
                bind(IMovieRepository::class.java).toProviderInstance {
                    object : IMovieRepository {
                        override fun getMovieInfo(imdbId: String): Flowable<DetailedMovieInfo> {
                            return Flowable.empty()
                        }

                        override fun getTopMovies(): Flowable<List<TopMovie>> {
                            val topMovie = TopMovie(
                                id,
                                title,
                                year,
                                poster,
                                rank,
                                imdbInfo
                            )

                            return Flowable.just(listOf(topMovie))
                        }
                    }
                }
            }
        })

        intentsRule.launchActivity(null)

        onView(withId(R.id.topmovies_activity_top_movies_recyclerview))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        intended(hasComponent(MovieActivity::class.java.name))
    }

}
