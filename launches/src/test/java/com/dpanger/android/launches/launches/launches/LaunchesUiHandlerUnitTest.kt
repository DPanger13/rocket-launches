package com.dpanger.android.launches.launches.launches

import com.dpanger.android.launches.data.launches.LaunchSummary
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime

internal class LaunchesUiHandlerUnitTest {

    private lateinit var uiHandler: LaunchesUiHandler
    private lateinit var actionSubscriber: TestSubscriber<Action>

    @Before
    fun SetUp() {
        uiHandler = LaunchesUiHandler()

        actionSubscriber = TestSubscriber()
    }

    @Test
    fun LaunchClicked_LaunchActionEmitted() {
        val launch = LaunchSummary(
            1234,
            "name",
            LocalDateTime.now()
        )
        uiHandler.actions.subscribe(actionSubscriber)

        uiHandler.movieClicked(launch)

        actionSubscriber.assertValue { it is ShowLaunch }
    }

}
