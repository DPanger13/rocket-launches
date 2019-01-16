package io.mercury.android.movies.topmovies.movie

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.mercury.android.movies.data.movies.DetailedMovieInfo
import io.mercury.android.movies.data.movies.IMovieRepository
import io.mercury.android.movies.data.movies.ImdbInfo
import io.mercury.android.movies.data.movies.TopMovie
import io.mercury.android.movies.topmovies.R
import io.reactivex.Flowable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import toothpick.Toothpick
import toothpick.config.Module
import java.net.URI

@RunWith(AndroidJUnit4::class)
class MovieInstrumentationTest {

    @get:Rule
    val intentRule = IntentsTestRule(MovieActivity::class.java, false, false)

    @Test
    fun Init_SucessfullyLoadedData_AllMovieInfoShown() {
        val id = "tt0001"
        val title = "title"
        val year = 2008
        val poster = URI("example.com")
        val contentRating = DetailedMovieInfo.ContentRating.R
        val releaseDate = LocalDate.now()
        val runtime = 123
        val genres = listOf("genre1")
        val director = "director"
        val writers = listOf("writer")
        val actors = listOf("actor1")
        val plot = "some stuff happened"
        val languages = listOf("english", "chinese")
        val country = "Us"
        val awards = "1 big award. 1 small award"
        val metascore = 23
        val rating = 8.3
        val votes = 208384

        Toothpick.openScope(MovieActivity::class.java).installTestModules(object : Module() {
            init {
                bind(IMovieRepository::class.java).toProviderInstance {
                    object : IMovieRepository {
                        override fun getMovieInfo(imdbId: String): Flowable<DetailedMovieInfo> {
                            val imdbInfo = ImdbInfo(id, rating, votes)
                            val movieInfo = DetailedMovieInfo(
                                id,
                                title,
                                year,
                                poster,
                                imdbInfo,
                                contentRating,
                                releaseDate,
                                runtime,
                                genres,
                                director,
                                writers,
                                actors,
                                plot,
                                languages,
                                country,
                                awards,
                                metascore
                            )

                            return Flowable.just(movieInfo)
                        }

                        override fun getTopMovies(): Flowable<List<TopMovie>> {
                            throw UnsupportedOperationException("This method shouldn't be called during test.")
                        }
                    }
                }
            }
        })

        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val intent = Intent(context, MovieActivity::class.java)
        intent.putExtra("extra_id_movie", "id")
        intentRule.launchActivity(intent)

        // check that all pieces of info are in the view tree
        onView(withText(id))
        onView(withText(title))
        onView(withText(year.toString()))
        onView(withText(poster.toString()))
        onView(withText(contentRating.toString()))
        onView(withText(releaseDate.toString()))
        onView(withText(runtime.toString()))
        onView(withText(genres.toString()))
        onView(withText(director))
        onView(withText(writers.toString()))
        onView(withText(actors.toString()))
        onView(withText(languages.toString()))
        onView(withText(country))
        onView(withText(awards))
        onView(withText(metascore.toString()))
        onView(withText(rating.toString()))
        onView(withText(votes.toString()))
    }

    @Test
    fun Init_LoadingDataFailed_RepositoryError_ErrorShown() {
        Toothpick.openScope(MovieActivity::class.java).installTestModules(object : Module() {
            init {
                bind(IMovieRepository::class.java).toProviderInstance {
                    object : IMovieRepository {
                        override fun getMovieInfo(imdbId: String): Flowable<DetailedMovieInfo> {
                            return Flowable.error(Exception())
                        }

                        override fun getTopMovies(): Flowable<List<TopMovie>> {
                            throw UnsupportedOperationException("This method shouldn't be called during test.")
                        }
                    }
                }
            }
        })

        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val intent = Intent(context, MovieActivity::class.java)
        intent.putExtra("extra_id_movie", "id")
        intentRule.launchActivity(intent)

        onView(withText(R.string.error)).check(matches(isDisplayed()))
    }

}
