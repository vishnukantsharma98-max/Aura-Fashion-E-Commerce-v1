package com.example.firebase

import android.util.Log
import com.example.data.MockData
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Idempotent utility to seed sample products and categories into Cloud Firestore.
 * - Uses deterministic document IDs (e.g., "cat_men", "prod_1") to prevent duplicates.
 * - Does not overwrite existing data unless explicitly requested.
 * - Gracefully handles missing Firebase credentials or network failures.
 */
object FirestoreCatalogSeeder {

    private const val TAG = "FirestoreCatalogSeeder"

    /**
     * Seeds the catalog into Firestore only if the 'products' collection is currently empty.
     * @return Result containing number of products seeded, or error.
     */
    suspend fun seedCatalogIfEmpty(): Result<Int> {
        val firestore = FirebaseConfig.getFirestore()
            ?: return Result.failure(IllegalStateException("Firebase is not initialized. Please provide google-services.json."))

        return try {
            val existing = firestore.collection("products").limit(1).get().await()
            if (!existing.isEmpty) {
                Log.d(TAG, "Products collection is already populated. Skipping automatic seeding.")
                return Result.success(0)
            }
            seedCatalog(forceOverwrite = false)
        } catch (e: Exception) {
            Log.w(TAG, "Failed checking existing catalog in Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Seeds products and categories into Cloud Firestore with deterministic IDs.
     * Idempotent: Can be called multiple times without creating duplicates.
     */
    suspend fun seedCatalog(forceOverwrite: Boolean = false): Result<Int> {
        val firestore = FirebaseConfig.getFirestore()
            ?: return Result.failure(IllegalStateException("Firebase is not initialized. Please provide google-services.json."))

        return try {
            val batch = firestore.batch()

            // 1. Seed Categories
            for (category in MockData.categories) {
                val catRef = firestore.collection("categories").document(category.id)
                val catData = hashMapOf(
                    "id" to category.id,
                    "name" to category.name,
                    "description" to category.description,
                    "imageUrl" to category.imageUrl,
                    "itemCount" to category.itemCount,
                    "displayOrder" to category.displayOrder,
                    "updatedAt" to System.currentTimeMillis()
                )
                if (forceOverwrite) {
                    batch.set(catRef, catData)
                } else {
                    batch.set(catRef, catData, SetOptions.merge())
                }
            }

            // 2. Seed Products
            for (product in MockData.products) {
                val prodRef = firestore.collection("products").document(product.id)
                val colorsMap = product.availableColors.map { color ->
                    hashMapOf(
                        "name" to color.name,
                        "hexCode" to color.hexCode
                    )
                }

                val prodData = hashMapOf(
                    "id" to product.id,
                    "name" to product.name,
                    "description" to product.description,
                    "categoryId" to product.categoryId,
                    "categoryName" to product.categoryName,
                    "category" to product.category,
                    "price" to product.price,
                    "originalPrice" to product.originalPrice,
                    "discountPercentage" to product.discountPercentage,
                    "rating" to product.rating,
                    "reviewCount" to product.reviewCount,
                    "stockQuantity" to product.stockQuantity,
                    "availableSizes" to product.availableSizes,
                    "availableColors" to colorsMap,
                    "imageUrl" to product.imageUrl,
                    "additionalImageUrls" to product.additionalImageUrls,
                    "isFeatured" to product.isFeatured,
                    "isPopular" to product.isPopular,
                    "createdAt" to product.createdAt
                )

                if (forceOverwrite) {
                    batch.set(prodRef, prodData)
                } else {
                    batch.set(prodRef, prodData, SetOptions.merge())
                }
            }

            batch.commit().await()
            Log.i(TAG, "Successfully seeded ${MockData.products.size} products and ${MockData.categories.size} categories.")
            Result.success(MockData.products.size)
        } catch (e: Exception) {
            Log.e(TAG, "Failed seeding Firestore catalog: ${e.message}", e)
            Result.failure(e)
        }
    }
}
