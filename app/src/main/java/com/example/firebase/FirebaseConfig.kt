package com.example.firebase

import android.content.Context
import android.util.Log
import com.example.AuraApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Safe accessor for Firebase services.
 * Ensures the app will never crash if Firebase credentials or google-services.json are missing.
 */
object FirebaseConfig {
    private const val TAG = "FirebaseConfig"

    fun initialize(context: Context): Boolean {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            true
        } catch (t: Throwable) {
            Log.w(TAG, "Firebase initialization error: ${t.message}")
            false
        }
    }

    val isAvailable: Boolean
        get() = try {
            val app = try {
                FirebaseApp.getInstance()
            } catch (e: Exception) {
                val context = try { AuraApplication.instance } catch (ignored: Exception) { null }
                if (context != null) {
                    if (FirebaseApp.getApps(context).isEmpty()) {
                        FirebaseApp.initializeApp(context)
                    } else {
                        FirebaseApp.getInstance()
                    }
                } else null
            }
            app != null
        } catch (t: Throwable) {
            Log.w(TAG, "Firebase is not initialized: ${t.message}")
            false
        }

    fun getAuth(): FirebaseAuth? {
        return if (isAvailable) {
            try {
                FirebaseAuth.getInstance()
            } catch (t: Throwable) {
                Log.w(TAG, "FirebaseAuth not available: ${t.message}")
                null
            }
        } else null
    }

    fun getFirestore(): FirebaseFirestore? {
        return if (isAvailable) {
            try {
                FirebaseFirestore.getInstance()
            } catch (t: Throwable) {
                Log.w(TAG, "FirebaseFirestore not available: ${t.message}")
                null
            }
        } else null
    }

    fun getStorage(): FirebaseStorage? {
        return if (isAvailable) {
            try {
                FirebaseStorage.getInstance()
            } catch (t: Throwable) {
                Log.w(TAG, "FirebaseStorage not available: ${t.message}")
                null
            }
        } else null
    }
}
