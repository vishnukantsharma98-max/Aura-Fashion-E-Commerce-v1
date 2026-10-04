package com.example.repository

import android.util.Log
import com.example.firebase.FirebaseConfig
import com.example.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FirebaseWishlistRepository(
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : WishlistRepository {

    private val TAG = "FirebaseWishlistRepo"
    private val _wishlistIds = MutableStateFlow<Set<String>>(setOf("prod_1", "prod_5"))
    private var wishlistListener: ListenerRegistration? = null

    private val firestore: FirebaseFirestore?
        get() = FirebaseConfig.getFirestore()

    init {
        scope.launch {
            authRepository.authState.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        attachWishlistListener(authState.user.id)
                    }
                    is AuthState.Guest -> {
                        detachListener()
                    }
                    is AuthState.Idle -> {
                        detachListener()
                        _wishlistIds.value = emptySet()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun detachListener() {
        wishlistListener?.remove()
        wishlistListener = null
    }

    private fun attachWishlistListener(userId: String) {
        val db = firestore ?: return
        detachListener()

        try {
            wishlistListener = db.collection("wishlists").document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Wishlist listen failed: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val idsList = (snapshot.get("productIds") as? List<*>)?.mapNotNull { it as? String }
                        if (idsList != null) {
                            _wishlistIds.value = idsList.toSet()
                        }
                    }
                }
        } catch (t: Throwable) {
            Log.w(TAG, "Could not attach wishlist listener: ${t.message}")
        }
    }

    override fun getWishlistProductIds(): Flow<Set<String>> = _wishlistIds.asStateFlow()

    override fun getWishlistProducts(): Flow<List<Product>> =
        combine(_wishlistIds, productRepository.getAllProducts()) { ids, allProducts ->
            allProducts.filter { ids.contains(it.id) }
        }

    override fun toggleWishlist(productId: String) {
        val current = _wishlistIds.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _wishlistIds.value = current

        val currentUser = authRepository.currentUser
        if (firestore != null && currentUser != null && !authRepository.isGuest) {
            scope.launch {
                try {
                    firestore?.collection("wishlists")?.document(currentUser.id)
                        ?.set(mapOf("userId" to currentUser.id, "productIds" to current.toList()))
                } catch (e: Exception) {
                    Log.w(TAG, "Sync wishlist failed: ${e.message}")
                }
            }
        }
    }

    override fun isWishlisted(productId: String): Boolean =
        _wishlistIds.value.contains(productId)
}
