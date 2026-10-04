package com.example.model

data class Order(
    val id: String,
    val orderNumber: String,
    val date: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val shipping: Double,
    val totalAmount: Double,
    val status: OrderStatus,
    val shippingAddress: ShippingAddress,
    val userId: String = "",
    val discount: Double = 0.0,
    val paymentMethod: String = "Credit Card",
    val createdAt: Long = System.currentTimeMillis()
)

enum class OrderStatus {
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

data class ShippingAddress(
    val fullName: String,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val phone: String
)
