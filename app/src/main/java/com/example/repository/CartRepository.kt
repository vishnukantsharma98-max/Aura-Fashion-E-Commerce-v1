package com.example.repository

import com.example.data.MockData
import com.example.model.CartItem
import com.example.model.Product
import com.example.model.ProductColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    fun getCartItemCount(): Flow<Int>
    fun getSubtotal(): Flow<Double>
    fun addToCart(product: Product, size: String, color: ProductColor, quantity: Int = 1)
    fun updateQuantity(cartItemId: String, newQuantity: Int)
    fun removeFromCart(cartItemId: String)
    fun clearCart()
}

class InMemoryCartRepository : CartRepository {
    private val _items = MutableStateFlow<List<CartItem>>(MockData.sampleCartItems)

    override fun getCartItems(): Flow<List<CartItem>> = _items.asStateFlow()

    override fun getCartItemCount(): Flow<Int> {
        return _items.map { list -> list.sumOf { it.quantity } }
    }

    override fun getSubtotal(): Flow<Double> {
        return _items.map { list -> list.sumOf { it.totalPrice } }
    }

    override fun addToCart(product: Product, size: String, color: ProductColor, quantity: Int) {
        val currentList = _items.value.toMutableList()
        val existingIndex = currentList.indexOfFirst {
            it.product.id == product.id && it.selectedSize == size && it.selectedColor.hexCode == color.hexCode
        }
        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            val newItem = CartItem(
                id = UUID.randomUUID().toString(),
                product = product,
                selectedSize = size,
                selectedColor = color,
                quantity = quantity
            )
            currentList.add(0, newItem)
        }
        _items.value = currentList
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
    }

    override fun removeFromCart(cartItemId: String) {
        _items.value = _items.value.filterNot { it.id == cartItemId }
    }

    override fun clearCart() {
        _items.value = emptyList()
    }
}
