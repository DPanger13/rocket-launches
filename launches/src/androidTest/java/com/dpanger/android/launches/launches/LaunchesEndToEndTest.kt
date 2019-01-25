package com.dpanger.android.launches.launches

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dpanger.android.launches.launches.launch.LaunchActivity
import com.dpanger.android.launches.launches.launches.LaunchesActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LaunchesEndToEndTest {

    @get:Rule
    val intentsRule = IntentsTestRule(LaunchesActivity::class.java)

    @Test
    fun StartLaunchesScreen_ClickLaunch_LaunchDetailsShow() {
        onView(withId(R.id.launches_activity_launches_recyclerview))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // make sure the movie screen launches
        intended(hasComponent(LaunchActivity::class.java.name))
    }

}
