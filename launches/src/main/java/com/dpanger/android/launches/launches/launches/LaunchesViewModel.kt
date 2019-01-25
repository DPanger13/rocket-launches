package com.dpanger.android.launches.launches.launches

import com.dpanger.android.launches.data.launches.ILaunchRepository
import com.dpanger.android.launches.data.launches.PagedLaunchSummary
import io.reactivex.Flowable
import javax.inject.Inject

internal class LaunchesViewModel @Inject constructor(private val repository: ILaunchRepository) {

    val topMovies: Flowable<PagedLaunchSummary> = repository.getUpcomingLaunches()

}
