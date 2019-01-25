package com.dpanger.android.launches.launches.launch

import com.dpanger.android.launches.data.launches.ILaunchRepository
import com.dpanger.android.launches.data.launches.Launch
import io.reactivex.Flowable
import javax.inject.Inject

internal class LaunchViewModel @Inject constructor(private val repository: ILaunchRepository) {

    fun getLaunch(id: Int): Flowable<Launch> = repository.getLaunch(id)

}
