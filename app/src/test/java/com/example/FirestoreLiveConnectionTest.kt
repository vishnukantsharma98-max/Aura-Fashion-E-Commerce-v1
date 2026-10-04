package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.firebase.FirebaseConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FirestoreLiveConnectionTest {

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<AuraApplication>()
        FirebaseConfig.initialize(app)
    }

    @Test
    fun testLiveFirestoreConnection() {
        val firestore = FirebaseConfig.getFirestore()
        println("Firestore instance retrieved: $firestore")
        try {
            runBlocking {
                val snapshot = firestore?.collection("categories")?.limit(1)?.get(Source.SERVER)?.await()
                println("SERVER READ SUCCESS! Snapshot size: ${snapshot?.size()}")
            }
        } catch (e: Exception) {
            println("SERVER READ EXCEPTION: [${e.javaClass.simpleName}] ${e.message}")
            e.printStackTrace()
        }
    }
}
