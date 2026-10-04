package com.example.repository

import com.example.data.MockData
import com.example.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

interface WishlistRepository {
    fun getWishlistProductIds(): Flow<Set<String>>
    fun getWishlistProducts(): Flow<List<Product>>
    fun toggleWishlist(productId: String)
    fun isWishlisted(productId: String): Boolean
}

class InMemoryWishlistRepository(
    private val productRepository: ProductRepository
) : WishlistRepository {
    // Initial wishlist with 2 items
    private val _wishlistIds = MutableStateFlow<Set<String>>(setOf("prod_1", "prod_5"))

    override fun getWishlistProductIds(): Flow<Set<String>> = _wishlistIds.asStateFlow()

    override fun getWishlistProducts(): Flow<List<Product>> {
        return productRepository.getAllProducts().map { allProducts ->
            val ids = _wishlistIds.value
            allProducts.filter { it.id in ids }
        }
    }

    override fun toggleWishlist(productId: String) {
        val current = _wishlistIds.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _wishlistIds.value = current
    }

    override fun isWishlisted(productId: String): Boolean {
        return _wishlistIds.value.contains(productId)
    }
}
