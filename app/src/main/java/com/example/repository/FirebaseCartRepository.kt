package com.example.repository

import android.util.Log
import com.example.data.MockData
import com.example.firebase.FirebaseConfig
import com.example.model.CartItem
import com.example.model.Product
import com.example.model.ProductColor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class FirebaseCartRepository(
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : CartRepository {

    private val TAG = "FirebaseCartRepo"
    private val _items = MutableStateFlow<List<CartItem>>(MockData.sampleCartItems)
    private var cartListener: ListenerRegistration? = null

    private val firestore: FirebaseFirestore?
        get() = FirebaseConfig.getFirestore()

    init {
        // Observe auth state changes to reload user cart
        scope.launch {
            authRepository.authState.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        attachUserCartListener(authState.user.id)
                    }
                    is AuthState.Guest -> {
                        detachListener()
                        // In guest mode, keep current items or empty
                    }
                    is AuthState.Idle -> {
                        detachListener()
                        _items.value = emptyList()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun detachListener() {
        cartListener?.remove()
        cartListener = null
    }

    private fun attachUserCartListener(userId: String) {
        val db = firestore ?: return
        detachListener()

        try {
            cartListener = db.collection("cart")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Cart listen failed: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        scope.launch {
                            val itemsList = snapshot.documents.mapNotNull { doc ->
                                try {
                                    val prodId = doc.getString("productId") ?: return@mapNotNull null
                                    val size = doc.getString("selectedSize") ?: "M"
                                    val colorMap = doc.get("selectedColor") as? Map<*, *>
                                    val color = if (colorMap != null) {
                                        ProductColor(
                                            name = colorMap["name"] as? String ?: "Default",
                                            hexCode = (colorMap["hexCode"] as? Number)?.toLong() ?: 0xFF000000
                                        )
                                    } else ProductColor("Default", 0xFF000000)
                                    val qty = doc.getLong("quantity")?.toInt() ?: 1

                                    // Look up product from local catalogue or snapshot
                                    val product = MockData.products.find { it.id == prodId }
                                        ?: parseEmbeddedProduct(doc)
                                        ?: return@mapNotNull null

                                    CartItem(
                                        id = doc.id,
                                        product = product,
                                        selectedSize = size,
                                        selectedColor = color,
                                        quantity = qty
                                    )
                                } catch (t: Throwable) {
                                    null
                                }
                            }
                            _items.value = itemsList
                        }
                    }
                }
        } catch (t: Throwable) {
            Log.w(TAG, "Could not attach cart listener: ${t.message}")
        }
    }

    private fun parseEmbeddedProduct(doc: com.google.firebase.firestore.DocumentSnapshot): Product? {
        val map = doc.get("product") as? Map<*, *> ?: return null
        return try {
            val sizes = (map["availableSizes"] as? List<*>)?.mapNotNull { it as? String } ?: listOf("S", "M", "L")
            val colorsList = (map["availableColors"] as? List<*>)?.mapNotNull { item ->
                when (item) {
                    is Map<*, *> -> {
                        val n = item["name"] as? String ?: "Color"
                        val h = (item["hexCode"] as? Number)?.toLong() ?: 0xFF000000
                        com.example.model.ProductColor(n, h)
                    }
                    else -> null
                }
            } ?: listOf(com.example.model.ProductColor("Classic", 0xFF1E1E1E))

            Product(
                id = map["id"] as? String ?: doc.id,
                name = map["name"] as? String ?: "Fashion Item",
                description = map["description"] as? String ?: "",
                price = (map["price"] as? Number)?.toDouble() ?: 0.0,
                originalPrice = (map["originalPrice"] as? Number)?.toDouble() ?: 0.0,
                discountPercentage = (map["discountPercentage"] as? Number)?.toInt() ?: 0,
                imageUrl = map["imageUrl"] as? String ?: "",
                category = map["category"] as? String ?: "All",
                rating = (map["rating"] as? Number)?.toDouble() ?: 5.0,
                reviewCount = (map["reviewCount"] as? Number)?.toInt() ?: 1,
                availableSizes = sizes,
                availableColors = colorsList,
                stockQuantity = (map["stockQuantity"] as? Number)?.toInt() ?: 10
            )
        } catch (ignored: Exception) {
            null
        }
    }

    override fun getCartItems(): Flow<List<CartItem>> = _items.asStateFlow()

    override fun getCartItemCount(): Flow<Int> =
        _items.map { list -> list.sumOf { it.quantity } }

    override fun getSubtotal(): Flow<Double> =
        _items.map { list -> list.sumOf { it.totalPrice } }

    override fun addToCart(product: Product, size: String, color: ProductColor, quantity: Int) {
        val currentList = _items.value.toMutableList()
        val existingIndex = currentList.indexOfFirst {
            it.product.id == product.id && it.selectedSize == size && it.selectedColor.hexCode == color.hexCode
        }

        val currentUser = authRepository.currentUser
        val isFirebaseActive = firestore != null && currentUser != null && !authRepository.isGuest

        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            val updatedQty = existing.quantity + quantity
            currentList[existingIndex] = existing.copy(quantity = updatedQty)
            _items.value = currentList

            if (isFirebaseActive) {
                scope.launch {
                    try {
                        firestore?.collection("cart")?.document(existing.id)
                            ?.update("quantity", updatedQty)
                    } catch (e: Exception) {
                        Log.w(TAG, "Cart update failed: ${e.message}")
                    }
                }
            }
        } else {
            val itemId = UUID.randomUUID().toString()
            val newItem = CartItem(
                id = itemId,
                product = product,
                selectedSize = size,
                selectedColor = color,
                quantity = quantity
            )
            currentList.add(0, newItem)
            _items.value = currentList

            if (isFirebaseActive) {
                scope.launch {
                    try {
                        val cartDoc = hashMapOf(
                            "id" to itemId,
                            "userId" to currentUser.id,
                            "productId" to product.id,
                            "selectedSize" to size,
                            "selectedColor" to hashMapOf(
                                "name" to color.name,
                                "hexCode" to color.hexCode
                            ),
                            "quantity" to quantity,
                            "product" to hashMapOf(
                                "id" to product.id,
                                "name" to product.name,
                                "price" to product.price,
                                "imageUrl" to product.imageUrl,
                                "category" to product.category,
                                "categoryId" to product.categoryId,
                                "categoryName" to product.categoryName
                            ),
                            "updatedAt" to System.currentTimeMillis()
                        )
                        firestore?.collection("cart")?.document(itemId)?.set(cartDoc)
                    } catch (e: Exception) {
                        Log.w(TAG, "Cart doc add failed: ${e.message}")
                    }
                }
            }
        }
    }

    override fun updateQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItemId)
            return
        }
        val currentList = _items.value.map { item ->
            if (item.id == cartItemId) item.copy(quantity = newQuantity) else item
        }
        _items.value = currentList

        val currentUser = authRepository.currentUser
        if (firestore != null && currentUser != null && !authRepository.isGuest) {
            scope.launch {
                try {
                    firestore?.collection("cart")?.document(cartItemId)?.update("quantity", newQuantity)
                } catch (e: Exception) {
                    Log.w(TAG, "Quantity update failed: ${e.message}")
                }
            }
        }
    }

    override fun removeFromCart(cartItemId: String) {
        _items.value = _items.value.filterNot { it.id == cartItemId }

        val currentUser = authRepository.currentUser
        if (firestore != null && currentUser != null && !authRepository.isGuest) {
            scope.launch {
                try {
                    firestore?.collection("cart")?.document(cartItemId)?.delete()
                } catch (e: Exception) {
                    Log.w(TAG, "Cart item removal failed: ${e.message}")
                }
            }
        }
    }

    override fun clearCart() {
        val itemsToDelete = _items.value
        _items.value = emptyList()

        val currentUser = authRepository.currentUser
        if (firestore != null && currentUser != null && !authRepository.isGuest) {
            scope.launch {
                try {
                    val batch = firestore?.batch() ?: return@launch
                    itemsToDelete.forEach { item ->
                        val ref = firestore?.collection("cart")?.document(item.id)
                        if (ref != null) batch.delete(ref)
                    }
                    batch.commit()
                } catch (e: Exception) {
                    Log.w(TAG, "Clear cart batch failed: ${e.message}")
                }
            }
        }
    }
}
