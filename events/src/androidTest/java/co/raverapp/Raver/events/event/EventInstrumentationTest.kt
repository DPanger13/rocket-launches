package co.raverapp.Raver.events.event

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.raverapp.android.data.events.Event
import co.raverapp.android.data.events.Promoter
import co.raverapp.android.data.events.Venue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime
import java.net.URI

@RunWith(AndroidJUnit4::class)
class EventInstrumentationTest {

    @get:Rule
    val intentRule = IntentsTestRule(EventActivity::class.java, false, false)

    @Test
    fun Init_AllLaunchInfoShown() {
        val title = "event name"
        val event = Event(
            title,
            LocalDateTime.of(2008, 1, 1, 1, 1),
            mutableListOf(),
            Venue("", "", ""),
            Promoter(""),
            true,
            Event.MinAge.TWENTYONE,
            URI("example.com")
        )

        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val intent = Intent(context, EventActivity::class.java)
        intent.putExtra("extra_event", event)
        intentRule.launchActivity(intent)

        onView(withText(title))
    }

}
