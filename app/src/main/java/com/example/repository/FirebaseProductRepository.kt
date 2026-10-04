package com.example.repository

import android.util.Log
import com.example.data.MockData
import com.example.firebase.FirebaseConfig
import com.example.model.Category
import com.example.model.Product
import com.example.model.ProductColor
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FirebaseProductRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ProductRepository {

    private val TAG = "FirebaseProductRepo"
    private val _products = MutableStateFlow<List<Product>>(MockData.products)
    private val _categories = MutableStateFlow<List<Category>>(MockData.categories)

    private val firestore: FirebaseFirestore?
        get() = FirebaseConfig.getFirestore()

    init {
        loadData()
    }

    private fun loadData() {
        val db = firestore
        if (db == null) {
            Log.d(TAG, "Firestore not available; using bundled catalogue.")
            return
        }

        // Listen to products collection
        try {
            db.collection("products").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Products listener failed: ${error.message}; keeping local catalogue.")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    if (snapshot.isEmpty) {
                        Log.d(TAG, "Firestore products collection is empty; retaining fallback catalogue.")
                        _products.value = MockData.products
                    } else {
                        val firestoreProducts = snapshot.documents.mapNotNull { doc ->
                            parseProductDocument(doc)
                        }
                        if (firestoreProducts.isNotEmpty()) {
                            _products.value = firestoreProducts
                        } else {
                            Log.w(TAG, "No valid products parsed from Firestore; retaining fallback catalogue.")
                            _products.value = MockData.products
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Could not attach products listener: ${t.message}")
        }

        // Listen to categories collection
        try {
            db.collection("categories").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Categories listener failed: ${error.message}; keeping local categories.")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    if (snapshot.isEmpty) {
                        Log.d(TAG, "Firestore categories collection is empty; retaining fallback categories.")
                        _categories.value = MockData.categories
                    } else {
                        val firestoreCats = snapshot.documents.mapNotNull { doc ->
                            parseCategoryDocument(doc)
                        }.sortedBy { it.displayOrder }

                        if (firestoreCats.isNotEmpty()) {
                            _categories.value = firestoreCats
                        } else {
                            Log.w(TAG, "No valid categories parsed from Firestore; retaining fallback categories.")
                            _categories.value = MockData.categories
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Could not attach categories listener: ${t.message}")
        }
    }

    /**
     * Validates and parses Firestore document into a Product instance.
     * Rejects malformed documents (missing name, invalid price, etc.) to guarantee runtime safety.
     */
    private fun parseProductDocument(doc: DocumentSnapshot): Product? {
        return try {
            val name = doc.getString("name")?.trim()
            if (name.isNullOrEmpty()) {
                Log.w(TAG, "Product ${doc.id} skipped: missing or blank name.")
                return null
            }

            val rawPrice = doc.get("price")
            val price = when (rawPrice) {
                is Number -> rawPrice.toDouble()
                is String -> rawPrice.toDoubleOrNull() ?: -1.0
                else -> -1.0
            }
            if (price <= 0.0) {
                Log.w(TAG, "Product ${doc.id} skipped: invalid price ($rawPrice).")
                return null
            }

            val categoryId = doc.getString("categoryId") ?: ""
            val categoryName = doc.getString("categoryName") ?: doc.getString("category") ?: ""
            if (categoryId.isBlank() && categoryName.isBlank()) {
                Log.w(TAG, "Product ${doc.id} skipped: missing category.")
                return null
            }

            val imageUrl = doc.getString("imageUrl")?.trim()
            if (imageUrl.isNullOrEmpty()) {
                Log.w(TAG, "Product ${doc.id} skipped: missing imageUrl.")
                return null
            }

            val rawOrig = doc.get("originalPrice")
            val originalPrice = when (rawOrig) {
                is Number -> rawOrig.toDouble()
                is String -> rawOrig.toDoubleOrNull() ?: price
                else -> price
            }

            val discount = doc.getLong("discountPercentage")?.toInt()
                ?: if (originalPrice > price) (((originalPrice - price) / originalPrice) * 100).toInt() else 0

            val rating = (doc.getDouble("rating") ?: 4.5).coerceIn(1.0, 5.0)
            val reviewCount = (doc.getLong("reviewCount")?.toInt() ?: 12).coerceAtLeast(0)
            val stock = (doc.getLong("stockQuantity")?.toInt() ?: 20).coerceAtLeast(0)

            val sizes = (doc.get("availableSizes") as? List<*>)?.mapNotNull { it as? String }
                ?.takeIf { it.isNotEmpty() } ?: listOf("S", "M", "L")

            val colorsList = (doc.get("availableColors") as? List<*>)?.mapNotNull { item ->
                when (item) {
                    is Map<*, *> -> {
                        val cName = item["name"] as? String ?: "Color"
                        val hex = (item["hexCode"] as? Number)?.toLong() ?: 0xFF1C1C1E
                        ProductColor(cName, hex)
                    }
                    else -> null
                }
            }?.takeIf { it.isNotEmpty() } ?: listOf(ProductColor("Classic Black", 0xFF1C1C1E))

            val additionalImages = (doc.get("additionalImageUrls") as? List<*>)?.mapNotNull { it as? String }
                ?: emptyList()

            val isFeatured = doc.getBoolean("isFeatured") ?: false
            val isPopular = doc.getBoolean("isPopular") ?: false
            val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

            Product(
                id = doc.id,
                name = name,
                description = doc.getString("description") ?: "",
                categoryId = categoryId.ifBlank { categoryName.lowercase().replace(" ", "_") },
                categoryName = categoryName.ifBlank { categoryId },
                price = price,
                originalPrice = originalPrice,
                discountPercentage = discount,
                rating = rating,
                reviewCount = reviewCount,
                availableSizes = sizes,
                availableColors = colorsList,
                stockQuantity = stock,
                imageUrl = imageUrl,
                additionalImageUrls = additionalImages,
                isFeatured = isFeatured,
                isPopular = isPopular,
                createdAt = createdAt,
                category = categoryName.ifBlank { categoryId }
            )
        } catch (t: Throwable) {
            Log.w(TAG, "Error parsing product ${doc.id}: ${t.message}")
            null
        }
    }

    /**
     * Validates and parses Firestore document into a Category instance.
     */
    private fun parseCategoryDocument(doc: DocumentSnapshot): Category? {
        return try {
            val name = doc.getString("name")?.trim()
            if (name.isNullOrEmpty()) return null

            val imageUrl = doc.getString("imageUrl") ?: ""
            val itemCount = (doc.getLong("itemCount")?.toInt() ?: 0).coerceAtLeast(0)
            val description = doc.getString("description") ?: ""
            val displayOrder = doc.getLong("displayOrder")?.toInt() ?: 0

            Category(
                id = doc.id,
                name = name,
                imageUrl = imageUrl,
                itemCount = itemCount,
                description = description,
                displayOrder = displayOrder
            )
        } catch (t: Throwable) {
            Log.w(TAG, "Error parsing category ${doc.id}: ${t.message}")
            null
        }
    }

    override fun getAllProducts(): Flow<List<Product>> = _products.asStateFlow()

    override fun getProductById(productId: String): Flow<Product?> =
        _products.map { list -> list.find { it.id == productId } }

    override fun getCategories(): Flow<List<Category>> = _categories.asStateFlow()

    override fun getFeaturedProducts(): Flow<List<Product>> =
        _products.map { list -> list.filter { it.isFeatured } }

    override fun getPopularProducts(): Flow<List<Product>> =
        _products.map { list -> list.filter { it.isPopular } }

    override fun getProductsByCategory(categoryName: String): Flow<List<Product>> =
        _products.map { list ->
            if (categoryName.equals("All", ignoreCase = true) || categoryName.equals("cat_all", ignoreCase = true)) {
                list
            } else {
                list.filter {
                    it.categoryId.equals(categoryName, ignoreCase = true) ||
                    it.categoryName.equals(categoryName, ignoreCase = true) ||
                    it.category.equals(categoryName, ignoreCase = true)
                }
            }
        }

    override fun searchProducts(query: String): Flow<List<Product>> =
        _products.map { list ->
            if (query.isBlank()) list
            else {
                val q = query.trim().lowercase()
                list.filter {
                    it.name.lowercase().contains(q) ||
                    it.categoryName.lowercase().contains(q) ||
                    it.category.lowercase().contains(q) ||
                    it.categoryId.lowercase().contains(q) ||
                    it.description.lowercase().contains(q)
                }
            }
        }
}
