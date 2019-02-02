package co.raverapp.Raver.events.events

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
import co.raverapp.Raver.events.R
import co.raverapp.Raver.events.event.EventActivity
import co.raverapp.android.data.events.*
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime
import toothpick.Toothpick
import toothpick.config.Module
import java.net.URI

@RunWith(AndroidJUnit4::class)
class EventsInstrumentationTest {

    @get:Rule
    val intentsRule = IntentsTestRule(EventsActivity::class.java, false, false)

    @Test
    fun Init_SuccessfullyLoadedData_LaunchesShow() {
        val title = "event name"
        val event = Event(
            title,
            LocalDateTime.of(2008, 1, 1, 1, 1),
            mutableListOf(Artist("name", listOf("genre"))),
            Venue("", "", ""),
            Promoter(""),
            true,
            Event.MinAge.TWENTYONE,
            URI("example.com")
        )

        Toothpick.openScope(EventsActivity::class.java).installTestModules(object : Module() {
            init {
                bind(IEventRepository::class.java).toProviderInstance {
                    object : IEventRepository {
                        override fun getEvents(): Single<List<Event>> {
                            return Single.just(listOf(event))
                        }
                    }
                }
            }
        })

        intentsRule.launchActivity(null)

        onView(withText(title))
    }

    @Test
    fun Init_LoadingDataFailed_ErrorShows() {
        Toothpick.openScope(EventsActivity::class.java).installTestModules(object : Module() {
            init {
                bind(IEventRepository::class.java).toProviderInstance {
                    object : IEventRepository {
                        override fun getEvents(): Single<List<Event>> {
                            return Single.error(Exception())
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
        val event = Event(
            "",
            LocalDateTime.of(2008, 1, 1, 1, 1),
            mutableListOf(Artist("name", listOf("genre"))),
            Venue("", "", ""),
            Promoter(""),
            true,
            Event.MinAge.TWENTYONE,
            URI("example.com")
        )

        Toothpick.openScope(EventsActivity::class.java).installTestModules(object : Module() {
            init {
                bind(IEventRepository::class.java).toProviderInstance {
                    object : IEventRepository {
                        override fun getEvents(): Single<List<Event>> {
                            return Single.just(listOf(event))
                        }
                    }
                }
            }
        })

        intentsRule.launchActivity(null)

        Thread.sleep(1000)

        onView(withId(R.id.launches_activity_launches_recyclerview))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        intended(hasComponent(EventActivity::class.java.name))
    }

}
