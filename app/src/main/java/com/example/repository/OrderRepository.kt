package com.example.repository

import com.example.data.MockData
import com.example.model.CartItem
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.ShippingAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

interface OrderRepository {
    fun getOrders(): Flow<List<Order>>
    fun createOrder(
        items: List<CartItem>,
        subtotal: Double,
        shipping: Double,
        shippingAddress: ShippingAddress,
        discount: Double = 0.0,
        paymentMethod: String = "Credit Card"
    ): Order
}

class InMemoryOrderRepository : OrderRepository {
    private val _orders = MutableStateFlow<List<Order>>(MockData.sampleOrders)

    override fun getOrders(): Flow<List<Order>> = _orders.asStateFlow()

    override fun createOrder(
        items: List<CartItem>,
        subtotal: Double,
        shipping: Double,
        shippingAddress: ShippingAddress,
        discount: Double,
        paymentMethod: String
    ): Order {
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
        val orderNum = "AUR-" + (10000..99999).random()
        val newOrder = Order(
            id = UUID.randomUUID().toString(),
            orderNumber = orderNum,
            date = dateFormat.format(Date()),
            items = items,
            subtotal = subtotal,
            shipping = shipping,
            totalAmount = (subtotal - discount).coerceAtLeast(0.0) + shipping,
            status = OrderStatus.PROCESSING,
            shippingAddress = shippingAddress,
            discount = discount,
            paymentMethod = paymentMethod,
            createdAt = System.currentTimeMillis()
        )
        val current = _orders.value.toMutableList()
        current.add(0, newOrder)
        _orders.value = current
        return newOrder
    }
}
