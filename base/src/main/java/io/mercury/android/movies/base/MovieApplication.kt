package io.mercury.android.movies.base

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class MovieApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
    }

}
