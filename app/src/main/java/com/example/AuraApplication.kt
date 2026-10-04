package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class AuraApplication : Application() {

    companion object {
        lateinit var instance: AuraApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
            Log.i("AuraApplication", "Firebase initialized successfully: ${FirebaseApp.getInstance().name}")
        } catch (t: Throwable) {
            Log.w("AuraApplication", "Firebase initialization check: ${t.message}")
        }
    }
}
