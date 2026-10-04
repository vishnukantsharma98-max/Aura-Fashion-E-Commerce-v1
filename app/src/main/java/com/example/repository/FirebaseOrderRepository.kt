package com.example.repository

import android.util.Log
import com.example.data.MockData
import com.example.firebase.FirebaseConfig
import com.example.model.CartItem
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.Product
import com.example.model.ProductColor
import com.example.model.ShippingAddress
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FirebaseOrderRepository(
    private val authRepository: AuthRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : OrderRepository {

    private val TAG = "FirebaseOrderRepo"
    private val _orders = MutableStateFlow<List<Order>>(MockData.sampleOrders)
    private var ordersListener: ListenerRegistration? = null

    private val firestore: FirebaseFirestore?
        get() = FirebaseConfig.getFirestore()

    init {
        scope.launch {
            authRepository.authState.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        attachOrdersListener(authState.user.id)
                    }
                    is AuthState.Guest -> {
                        detachListener()
                        _orders.value = MockData.sampleOrders
                    }
                    is AuthState.Idle -> {
                        detachListener()
                        _orders.value = emptyList()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun detachListener() {
        ordersListener?.remove()
        ordersListener = null
    }

    private fun attachOrdersListener(userId: String) {
        val db = firestore ?: return
        detachListener()

        try {
            ordersListener = db.collection("orders")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Orders listen failed: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val parsed = snapshot.documents.mapNotNull { doc ->
                            try {
                                val addrMap = doc.get("shippingAddress") as? Map<*, *>
                                val address = if (addrMap != null) {
                                    ShippingAddress(
                                        fullName = addrMap["fullName"] as? String ?: "Client",
                                        street = addrMap["street"] as? String ?: "",
                                        city = addrMap["city"] as? String ?: "",
                                        state = addrMap["state"] as? String ?: "",
                                        zipCode = addrMap["zipCode"] as? String ?: "",
                                        phone = addrMap["phone"] as? String ?: ""
                                    )
                                } else MockData.sampleUser.defaultAddress

                                val itemsList = (doc.get("items") as? List<*>)?.mapNotNull { itemMap ->
                                    if (itemMap is Map<*, *>) {
                                        val prodMap = itemMap["product"] as? Map<*, *>
                                        val prodId = itemMap["productId"] as? String ?: prodMap?.get("id") as? String ?: ""
                                        val product = MockData.products.find { it.id == prodId } ?: Product(
                                            id = prodId,
                                            name = prodMap?.get("name") as? String ?: "Piece",
                                            description = "",
                                            price = (prodMap?.get("price") as? Number)?.toDouble() ?: 0.0,
                                            originalPrice = 0.0,
                                            discountPercentage = 0,
                                            imageUrl = prodMap?.get("imageUrl") as? String ?: "",
                                            category = prodMap?.get("category") as? String ?: "All",
                                            rating = 5.0,
                                            reviewCount = 1,
                                            availableSizes = listOf("S", "M", "L"),
                                            availableColors = listOf(ProductColor("Default", 0xFF000000)),
                                            stockQuantity = 10
                                        )
                                        val colMap = itemMap["selectedColor"] as? Map<*, *>
                                        val color = if (colMap != null) {
                                            ProductColor(
                                                name = colMap["name"] as? String ?: "Default",
                                                hexCode = (colMap["hexCode"] as? Number)?.toLong() ?: 0xFF000000
                                            )
                                        } else ProductColor("Default", 0xFF000000)

                                        CartItem(
                                            id = itemMap["id"] as? String ?: UUID.randomUUID().toString(),
                                            product = product,
                                            selectedSize = itemMap["selectedSize"] as? String ?: "M",
                                            selectedColor = color,
                                            quantity = (itemMap["quantity"] as? Number)?.toInt() ?: 1
                                        )
                                    } else null
                                } ?: emptyList()

                                val statusStr = doc.getString("status") ?: "PROCESSING"
                                val status = try {
                                    OrderStatus.valueOf(statusStr.uppercase())
                                } catch (e: Exception) {
                                    OrderStatus.PROCESSING
                                }

                                Order(
                                    id = doc.id,
                                    orderNumber = doc.getString("orderNumber") ?: doc.id,
                                    date = doc.getString("date") ?: "",
                                    items = itemsList,
                                    subtotal = doc.getDouble("subtotal") ?: 0.0,
                                    shipping = doc.getDouble("shipping") ?: 0.0,
                                    totalAmount = doc.getDouble("totalAmount") ?: 0.0,
                                    status = status,
                                    shippingAddress = address,
                                    userId = doc.getString("userId") ?: "",
                                    discount = doc.getDouble("discount") ?: 0.0,
                                    paymentMethod = doc.getString("paymentMethod") ?: "Credit Card",
                                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                                )
                            } catch (t: Throwable) {
                                null
                            }
                        }
                        if (parsed.isNotEmpty()) {
                            _orders.value = parsed.sortedByDescending { it.createdAt }
                        }
                    }
                }
        } catch (t: Throwable) {
            Log.w(TAG, "Could not attach orders listener: ${t.message}")
        }
    }

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
        val orderId = UUID.randomUUID().toString()
        val currentUser = authRepository.currentUser
        val userId = currentUser?.id ?: "guest"
        val total = (subtotal - discount).coerceAtLeast(0.0) + shipping

        val newOrder = Order(
            id = orderId,
            orderNumber = orderNum,
            date = dateFormat.format(Date()),
            items = items,
            subtotal = subtotal,
            shipping = shipping,
            totalAmount = total,
            status = OrderStatus.PROCESSING,
            shippingAddress = shippingAddress,
            userId = userId,
            discount = discount,
            paymentMethod = paymentMethod,
            createdAt = System.currentTimeMillis()
        )

        val current = _orders.value.toMutableList()
        current.add(0, newOrder)
        _orders.value = current

        if (firestore != null && currentUser != null && !authRepository.isGuest) {
            scope.launch {
                try {
                    val orderMap = hashMapOf(
                        "id" to orderId,
                        "orderNumber" to orderNum,
                        "date" to newOrder.date,
                        "userId" to userId,
                        "subtotal" to subtotal,
                        "discount" to discount,
                        "shipping" to shipping,
                        "totalAmount" to total,
                        "status" to "PROCESSING",
                        "paymentMethod" to paymentMethod,
                        "createdAt" to newOrder.createdAt,
                        "shippingAddress" to hashMapOf(
                            "fullName" to shippingAddress.fullName,
                            "street" to shippingAddress.street,
                            "city" to shippingAddress.city,
                            "state" to shippingAddress.state,
                            "zipCode" to shippingAddress.zipCode,
                            "phone" to shippingAddress.phone
                        ),
                        "items" to items.map { item ->
                            hashMapOf(
                                "id" to item.id,
                                "productId" to item.product.id,
                                "selectedSize" to item.selectedSize,
                                "selectedColor" to hashMapOf(
                                    "name" to item.selectedColor.name,
                                    "hexCode" to item.selectedColor.hexCode
                                ),
                                "quantity" to item.quantity,
                                "product" to hashMapOf(
                                    "id" to item.product.id,
                                    "name" to item.product.name,
                                    "price" to item.product.price,
                                    "imageUrl" to item.product.imageUrl,
                                    "category" to item.product.category,
                                    "categoryId" to item.product.categoryId,
                                    "categoryName" to item.product.categoryName
                                )
                            )
                        }
                    )
                    firestore?.collection("orders")?.document(orderId)?.set(orderMap)
                } catch (e: Exception) {
                    Log.w(TAG, "Order save to Firestore failed: ${e.message}")
                }
            }
        }

        return newOrder
    }
}
