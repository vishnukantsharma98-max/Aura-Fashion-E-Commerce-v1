package com.example.model

data class CartItem(
    val id: String,
    val product: Product,
    val selectedSize: String,
    val selectedColor: ProductColor,
    val quantity: Int
) {
    val totalPrice: Double
        get() = product.price * quantity
}
