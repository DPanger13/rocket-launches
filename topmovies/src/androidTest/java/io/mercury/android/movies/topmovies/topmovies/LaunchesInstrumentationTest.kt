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
import com.dpanger.android.launches.data.launches.ILaunchRepository
import com.dpanger.android.launches.data.launches.Launch
import com.dpanger.android.launches.data.launches.LaunchSummary
import com.dpanger.android.launches.data.launches.PagedLaunchSummary
import io.mercury.android.movies.topmovies.R
import io.mercury.android.movies.topmovies.movie.LaunchActivity
import io.reactivex.Flowable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime
import toothpick.Toothpick
import toothpick.config.Module

@RunWith(AndroidJUnit4::class)
class LaunchesInstrumentationTest {

    @get:Rule
    val intentsRule = IntentsTestRule(LaunchesActivity::class.java, false, false)

    @Test
    fun Init_SuccessfullyLoadedData_LaunchesShow() {
        val id = 1234
        val name = "Launch 1"
        val dateTime = LocalDateTime.now()

        Toothpick.openScope(LaunchesActivity::class.java).installTestModules(object : Module() {
            init {
                bind(ILaunchRepository::class.java).toProviderInstance {
                    object : ILaunchRepository {
                        override fun getLaunch(id: Int): Flowable<Launch> {
                            throw UnsupportedOperationException("This method shouldn't be called during test.")
                        }

                        override fun getUpcomingLaunches(offset: Int?): Flowable<PagedLaunchSummary> {
                            val launches = listOf(LaunchSummary(
                                id,
                                name,
                                dateTime
                            ))
                            val pagedLaunchSummary = PagedLaunchSummary(
                                0,
                                1,
                                1,
                                launches
                            )

                            return Flowable.just(pagedLaunchSummary)
                        }
                    }
                }
            }
        })

        intentsRule.launchActivity(null)

        //check that all pieces of info are in the view tree
        onView(withText(id))
        onView(withText(name))
        onView(withText(dateTime.toString()))
    }

    @Test
    fun Init_LoadingDataFailed_ErrorShows() {
        Toothpick.openScope(LaunchesActivity::class.java).installTestModules(object : Module() {
            init {
                bind(ILaunchRepository::class.java).toProviderInstance {
                    object : ILaunchRepository {
                        override fun getLaunch(id: Int): Flowable<Launch> {
                            throw UnsupportedOperationException("This method shouldn't be called during test.")
                        }

                        override fun getUpcomingLaunches(offset: Int?): Flowable<PagedLaunchSummary> {
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
    fun LaunchClicked_DetailScreenShows() {
        Toothpick.openScope(LaunchesActivity::class.java).installTestModules(object : Module() {
            init {
                bind(ILaunchRepository::class.java).toProviderInstance {
                    object : ILaunchRepository {
                        override fun getLaunch(id: Int): Flowable<Launch> {
                            throw UnsupportedOperationException("This method shouldn't be called during test.")
                        }

                        override fun getUpcomingLaunches(offset: Int?): Flowable<PagedLaunchSummary> {
                            val id = 1234
                            val name = "Launch 1"
                            val dateTime = LocalDateTime.now()
                            val launches = listOf(LaunchSummary(
                                id,
                                name,
                                dateTime
                            ))
                            val pagedLaunchSummary = PagedLaunchSummary(
                                0,
                                1,
                                1,
                                launches
                            )

                            return Flowable.just(pagedLaunchSummary)
                        }
                    }
                }
            }
        })

        Toothpick.openScope(LaunchActivity::class.java).installTestModules(object : Module() {
            init {
                bind(ILaunchRepository::class.java).toProviderInstance {
                    object : ILaunchRepository {
                        override fun getUpcomingLaunches(offset: Int?): Flowable<PagedLaunchSummary> {
                            return Flowable.empty()
                        }

                        override fun getLaunch(id: Int): Flowable<Launch> {
                            val launchId = 2002
                            val name = "Launch 1"
                            val dateTime = LocalDateTime.now()
                            val locationName = "Location 2"
                            val rocketName = "Rocket 1"
                            val missionName = "Mission 3"
                            val launch = Launch(
                                launchId,
                                name,
                                dateTime,
                                Launch.Location(locationName),
                                Launch.Rocket(rocketName),
                                listOf(Launch.Mission(missionName))
                            )

                            return Flowable.just(launch)
                        }
                    }
                }
            }
        })

        intentsRule.launchActivity(null)

        onView(withId(R.id.launches_activity_launches_recyclerview))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        intended(hasComponent(LaunchActivity::class.java.name))
    }

}
