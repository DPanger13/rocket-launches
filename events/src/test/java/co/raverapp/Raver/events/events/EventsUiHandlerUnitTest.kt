package co.raverapp.Raver.events.events

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
            LocalDateTime.of(2008, 1, 1, 1, 1),
            mutableListOf(),
            Venue("", "", ""),
            Promoter(""),
            true,
            Event.MinAge.TWENTYONE,
            URI("example.com")
        )
        uiHandler.actions.subscribe(actionSubscriber)

        uiHandler.eventClicked(event)

        actionSubscriber.assertValue { it is ShowEvent }
    }

}
