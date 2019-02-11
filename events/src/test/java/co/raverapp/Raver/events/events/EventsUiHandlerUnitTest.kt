package co.raverapp.Raver.events.events

import co.raverapp.android.data.events.Artist
import co.raverapp.android.data.events.Event
import co.raverapp.android.data.events.Promoter
import co.raverapp.android.data.events.Venue
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.net.URI

internal class EventsUiHandlerUnitTest {

    private lateinit var uiHandler: LaunchesUiHandler
    private lateinit var actionSubscriber: TestSubscriber<Action>

    @Before
    fun SetUp() {
        uiHandler = LaunchesUiHandler()

        actionSubscriber = TestSubscriber()
    }

    @Test
    fun LaunchClicked_LaunchActionEmitted() {
        val event = Event(
            "",
            "01:00:00",
            listOf("2001-01-01"),
            listOf(Artist("", listOf(""))),
            Venue("", "", ""),
            listOf(Promoter("")),
            true,
            Event.MinAge.TWENTYONE,
            "example.com"
        )
        event.bannerUrl = "example.com"

        uiHandler.actions.subscribe(actionSubscriber)

        uiHandler.eventClicked(event)

        actionSubscriber.assertValue { it is ShowEvent }
    }

}
