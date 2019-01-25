package io.mercury.android.movies.topmovies.movie

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dpanger.android.launches.data.launches.ILaunchRepository
import com.dpanger.android.launches.data.launches.Launch
import com.dpanger.android.launches.data.launches.PagedLaunchSummary
import io.mercury.android.movies.topmovies.R
import io.reactivex.Flowable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime
import toothpick.Toothpick
import toothpick.config.Module

@RunWith(AndroidJUnit4::class)
class LaunchInstrumentationTest {

    @get:Rule
    val intentRule = IntentsTestRule(LaunchActivity::class.java, false, false)

    @Test
    fun Init_SucessfullyLoadedData_AllLaunchInfoShown() {
        val id = 2002
        val name = "Launch 1"
        val dateTime = LocalDateTime.now()
        val locationName = "Location 2"
        val rocketName = "Rocket 1"
        val missionName = "Mission 3"

        Toothpick.openScope(LaunchActivity::class.java).installTestModules(object : Module() {
            init {
                bind(ILaunchRepository::class.java).toProviderInstance {
                    object : ILaunchRepository {
                        override fun getLaunch(id: Int): Flowable<Launch> {
                            val launch = Launch(
                                id,
                                name,
                                dateTime,
                                Launch.Location(locationName),
                                Launch.Rocket(rocketName),
                                listOf(Launch.Mission(missionName))
                            )

                            return Flowable.just(launch)
                        }

                        override fun getUpcomingLaunches(offset: Int?): Flowable<PagedLaunchSummary> {
                            throw UnsupportedOperationException("This method shouldn't be called during test.")
                        }
                    }
                }
            }
        })

        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val intent = Intent(context, LaunchActivity::class.java)
        intent.putExtra("extra_id_launch", "id")
        intentRule.launchActivity(intent)

        // check that all pieces of info are in the view hierarchy
        onView(withText(id))
        onView(withText(name))
        onView(withText(dateTime.toString()))
        onView(withText(locationName))
        onView(withText(rocketName))
        onView(withText(missionName))
    }

    @Test
    fun Init_LoadingDataFailed_RepositoryError_ErrorShown() {
        Toothpick.openScope(LaunchActivity::class.java).installTestModules(object : Module() {
            init {
                bind(ILaunchRepository::class.java).toProviderInstance {
                    object : ILaunchRepository {
                        override fun getLaunch(id: Int): Flowable<Launch> {
                            return Flowable.error(Exception())
                        }

                        override fun getUpcomingLaunches(offset: Int?): Flowable<PagedLaunchSummary> {
                            throw UnsupportedOperationException("This method shouldn't be called during test.")
                        }
                    }
                }
            }
        })

        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val intent = Intent(context, LaunchActivity::class.java)
        intent.putExtra("extra_id_launch", "id")
        intentRule.launchActivity(intent)

        onView(withText(R.string.error)).check(matches(isDisplayed()))
    }

}
