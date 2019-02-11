package co.raverapp.Raver.base

import android.app.Application
import com.google.firebase.FirebaseApp

class RaverApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initFirebase()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
    }

}
