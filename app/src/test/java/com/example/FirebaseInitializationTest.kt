package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.firebase.FirebaseConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FirebaseInitializationTest {

    private lateinit var app: AuraApplication

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        FirebaseConfig.initialize(app)
    }

    @Test
    fun testFirebaseAppInitializesWithCorrectProject() {
        assertTrue("Firebase should be available", FirebaseConfig.isAvailable)
        val firebaseApp = FirebaseApp.getInstance()
        assertNotNull("FirebaseApp instance should not be null", firebaseApp)

        val options = firebaseApp.options
        assertEquals("assign-hub-4bd90", options.projectId)
        assertEquals("1:213358924635:android:1e0029cfb533f95628007e", options.applicationId)
        assertTrue(options.storageBucket?.contains("assign-hub-4bd90") == true)
    }

    @Test
    fun testFirebaseAuthInitializes() {
        val auth = FirebaseConfig.getAuth()
        assertNotNull("FirebaseAuth instance should initialize", auth)
        assertTrue(auth is FirebaseAuth)
        assertEquals(FirebaseApp.getInstance(), auth?.app)
    }

    @Test
    fun testFirebaseFirestoreInitializes() {
        val firestore = FirebaseConfig.getFirestore()
        assertNotNull("FirebaseFirestore instance should initialize", firestore)
        assertTrue(firestore is FirebaseFirestore)
        assertEquals(FirebaseApp.getInstance(), firestore?.app)
    }

    @Test
    fun testFirebaseStorageInitializes() {
        val storage = FirebaseConfig.getStorage()
        assertNotNull("FirebaseStorage instance should initialize", storage)
        assertTrue(storage is FirebaseStorage)
        assertEquals(FirebaseApp.getInstance(), storage?.app)
    }
}
