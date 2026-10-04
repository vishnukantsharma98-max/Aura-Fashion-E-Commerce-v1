package com.example.repository

import android.net.Uri
import com.example.firebase.FirebaseConfig
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface StorageRepository {
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String>
    suspend fun getProductImageUrl(imagePath: String): Result<String>
}

class FirebaseStorageRepository : StorageRepository {

    private val storage: FirebaseStorage?
        get() = FirebaseConfig.getStorage()

    override suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String> {
        val s = storage ?: return Result.failure(IllegalStateException("Firebase Storage is not configured."))
        return withContext(Dispatchers.IO) {
            try {
                val ref = s.reference.child("users/$userId/profile_${System.currentTimeMillis()}.jpg")
                ref.putFile(imageUri).await()
                val downloadUrl = ref.downloadUrl.await().toString()
                Result.success(downloadUrl)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getProductImageUrl(imagePath: String): Result<String> {
        val s = storage ?: return Result.failure(IllegalStateException("Firebase Storage is not configured."))
        return withContext(Dispatchers.IO) {
            try {
                val ref = if (imagePath.startsWith("gs://") || imagePath.startsWith("http")) {
                    s.getReferenceFromUrl(imagePath)
                } else {
                    s.reference.child("products/$imagePath")
                }
                val url = ref.downloadUrl.await().toString()
                Result.success(url)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
