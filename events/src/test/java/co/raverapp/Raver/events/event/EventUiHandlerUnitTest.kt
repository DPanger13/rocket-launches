package co.raverapp.Raver.events.event

import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test

class EventUiHandlerUnitTest {

    private lateinit var uiHandler: LaunchUiHandler
    private lateinit var actionSubscriber: TestSubscriber<Action>

    @Before
    fun SetUp() {
        uiHandler = LaunchUiHandler()

        actionSubscriber = TestSubscriber()
    }

    @Test
    fun BackClicked_BackActionEmitted() {
        uiHandler.actions.subscribe(actionSubscriber)

        uiHandler.backClicked()

        actionSubscriber.assertValue { it is NavigateBack }
    }

}
