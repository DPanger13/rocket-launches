package io.mercury.android.movies.topmovies.topmovies

import com.dpanger.android.launches.data.launches.ILaunchRepository
import com.dpanger.android.launches.data.launches.PagedLaunchSummary
import io.reactivex.Flowable
import javax.inject.Inject

internal class LaunchesViewModel @Inject constructor(private val repository: ILaunchRepository) {

    val topMovies: Flowable<PagedLaunchSummary> = repository.getUpcomingLaunches()

}
