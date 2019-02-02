package co.raverapp.Raver.base

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class LaunchApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
    }

}
