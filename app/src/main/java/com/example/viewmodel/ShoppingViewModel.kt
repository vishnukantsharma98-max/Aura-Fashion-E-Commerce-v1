package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MockData
import com.example.model.CartItem
import com.example.model.Category
import com.example.model.Order
import com.example.model.Product
import com.example.model.ProductColor
import com.example.model.ShippingAddress
import com.example.model.UserProfile
import com.example.repository.AuthRepository
import com.example.repository.AuthState
import com.example.repository.CartRepository
import com.example.repository.FirebaseAuthRepository
import com.example.repository.FirebaseCartRepository
import com.example.repository.FirebaseOrderRepository
import com.example.repository.FirebaseProductRepository
import com.example.repository.FirebaseStorageRepository
import com.example.repository.FirebaseWishlistRepository
import com.example.repository.OrderRepository
import com.example.repository.ProductRepository
import com.example.repository.StorageRepository
import com.example.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShoppingUiState(
    val products: List<Product> = emptyList(),
    val featuredProducts: List<Product> = emptyList(),
    val popularProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val filteredProducts: List<Product> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val cartCount: Int = 0,
    val subtotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val appliedPromoCode: String? = null,
    val shippingFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val wishlistIds: Set<String> = emptySet(),
    val wishlistProducts: List<Product> = emptyList(),
    val orders: List<Order> = emptyList(),
    val userProfile: UserProfile = MockData.sampleUser,
    val isLoggedIn: Boolean = true,
    val isGuestUser: Boolean = false,
    val isAuthLoading: Boolean = false,
    val authError: String? = null,
    val authState: AuthState = AuthState.Idle,
    val isDarkMode: Boolean = false,
    val selectedProduct: Product? = null,
    val lastPlacedOrder: Order? = null
)

class ShoppingViewModel(
    private val authRepository: AuthRepository = FirebaseAuthRepository(),
    private val productRepository: ProductRepository = FirebaseProductRepository(),
    private val cartRepository: CartRepository = FirebaseCartRepository(authRepository, productRepository),
    private val wishlistRepository: WishlistRepository = FirebaseWishlistRepository(authRepository, productRepository),
    private val orderRepository: OrderRepository = FirebaseOrderRepository(authRepository),
    private val storageRepository: StorageRepository = FirebaseStorageRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ShoppingUiState(
            products = MockData.products,
            featuredProducts = MockData.products.filter { it.isFeatured },
            popularProducts = MockData.products.filter { it.isPopular },
            categories = MockData.categories,
            filteredProducts = MockData.products,
            cartItems = MockData.sampleCartItems,
            cartCount = MockData.sampleCartItems.sumOf { it.quantity },
            subtotal = MockData.sampleCartItems.sumOf { it.totalPrice },
            discountAmount = 0.0,
            appliedPromoCode = null,
            shippingFee = 0.0,
            totalAmount = MockData.sampleCartItems.sumOf { it.totalPrice },
            wishlistIds = setOf("prod_1", "prod_5"),
            wishlistProducts = MockData.products.filter { it.id in setOf("prod_1", "prod_5") },
            orders = MockData.sampleOrders,
            userProfile = MockData.sampleUser,
            isLoggedIn = true,
            isGuestUser = false,
            isAuthLoading = false,
            authError = null,
            isDarkMode = false
        )
    )
    val uiState: StateFlow<ShoppingUiState> = _uiState.asStateFlow()

    init {
        // Observe Auth State
        viewModelScope.launch {
            authRepository.authState.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        _uiState.update {
                            it.copy(
                                authState = authState,
                                userProfile = authState.user,
                                isLoggedIn = true,
                                isGuestUser = false,
                                isAuthLoading = false,
                                authError = null
                            )
                        }
                    }
                    is AuthState.Guest -> {
                        _uiState.update {
                            it.copy(
                                authState = authState,
                                userProfile = authRepository.currentUser ?: MockData.sampleUser,
                                isLoggedIn = true,
                                isGuestUser = true,
                                isAuthLoading = false,
                                authError = null
                            )
                        }
                    }
                    is AuthState.Loading -> {
                        _uiState.update {
                            it.copy(
                                authState = authState,
                                isAuthLoading = true,
                                authError = null
                            )
                        }
                    }
                    is AuthState.Error -> {
                        _uiState.update {
                            it.copy(
                                authState = authState,
                                isAuthLoading = false,
                                authError = authState.message
                            )
                        }
                    }
                    is AuthState.Idle -> {
                        _uiState.update {
                            it.copy(
                                authState = authState,
                                isLoggedIn = false,
                                isGuestUser = false,
                                isAuthLoading = false,
                                authError = null
                            )
                        }
                    }
                }
            }
        }

        // Observe Products
        viewModelScope.launch {
            productRepository.getAllProducts().collect { allProducts ->
                _uiState.update { current ->
                    current.copy(
                        products = allProducts,
                        featuredProducts = allProducts.filter { it.isFeatured },
                        popularProducts = allProducts.filter { it.isPopular },
                        wishlistProducts = allProducts.filter { current.wishlistIds.contains(it.id) }
                    )
                }
                recalculateFiltered()
            }
        }

        // Observe Categories
        viewModelScope.launch {
            productRepository.getCategories().collect { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }

        // Observe Cart
        viewModelScope.launch {
            cartRepository.getCartItems().collect { cartList ->
                _uiState.update { current ->
                    val count = cartList.sumOf { it.quantity }
                    val sub = cartList.sumOf { it.totalPrice }
                    val discount = if (current.appliedPromoCode != null) sub * 0.10 else 0.0
                    val discountedSub = (sub - discount).coerceAtLeast(0.0)
                    val ship = if (sub >= 1999.0 || sub == 0.0) 0.0 else 149.0
                    val total = if (sub == 0.0) 0.0 else discountedSub + ship

                    current.copy(
                        cartItems = cartList,
                        cartCount = count,
                        subtotal = sub,
                        discountAmount = discount,
                        shippingFee = ship,
                        totalAmount = total
                    )
                }
            }
        }

        // Observe Wishlist IDs
        viewModelScope.launch {
            wishlistRepository.getWishlistProductIds().collect { ids ->
                _uiState.update { current ->
                    current.copy(
                        wishlistIds = ids,
                        wishlistProducts = current.products.filter { ids.contains(it.id) }
                    )
                }
            }
        }

        // Observe Orders
        viewModelScope.launch {
            orderRepository.getOrders().collect { orderList ->
                _uiState.update { it.copy(orders = orderList) }
            }
        }
    }

    private fun recalculateFiltered() {
        val state = _uiState.value
        val cat = state.selectedCategory
        val query = state.searchQuery.trim().lowercase()

        val filtered = state.products.filter { prod ->
            val matchCategory = if (cat.equals("All", ignoreCase = true) || cat.equals("cat_all", ignoreCase = true)) {
                true
            } else {
                prod.categoryId.equals(cat, ignoreCase = true) ||
                    prod.categoryName.equals(cat, ignoreCase = true) ||
                    prod.category.equals(cat, ignoreCase = true)
            }

            val matchQuery = if (query.isBlank()) {
                true
            } else {
                prod.name.lowercase().contains(query) ||
                    prod.categoryName.lowercase().contains(query) ||
                    prod.category.lowercase().contains(query) ||
                    prod.categoryId.lowercase().contains(query) ||
                    prod.description.lowercase().contains(query)
            }

            matchCategory && matchQuery
        }

        _uiState.update { it.copy(filteredProducts = filtered) }
    }

    fun selectCategory(categoryName: String) {
        _uiState.update { it.copy(selectedCategory = categoryName) }
        recalculateFiltered()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        recalculateFiltered()
    }

    fun selectProduct(product: Product?) {
        _uiState.update { it.copy(selectedProduct = product) }
    }

    fun selectProductById(productId: String) {
        val found = _uiState.value.products.find { it.id == productId }
            ?: MockData.products.find { it.id == productId }
        _uiState.update { it.copy(selectedProduct = found) }
    }

    fun addToCart(product: Product, size: String, color: ProductColor, quantity: Int = 1) {
        cartRepository.addToCart(product, size, color, quantity)
    }

    fun updateCartQuantity(cartItemId: String, newQuantity: Int) {
        cartRepository.updateQuantity(cartItemId, newQuantity)
    }

    fun removeFromCart(cartItemId: String) {
        cartRepository.removeFromCart(cartItemId)
    }

    fun toggleWishlist(productId: String) {
        wishlistRepository.toggleWishlist(productId)
    }

    fun isWishlisted(productId: String): Boolean {
        return wishlistRepository.isWishlisted(productId)
    }

    fun applyPromoCode(code: String): Boolean {
        val trimmed = code.trim().uppercase()
        if (trimmed == "AURA10" || trimmed == "VIP2026" || trimmed == "SAVE10") {
            _uiState.update { current ->
                val discount = current.subtotal * 0.10
                val discountedSub = (current.subtotal - discount).coerceAtLeast(0.0)
                val total = if (current.subtotal == 0.0) 0.0 else discountedSub + current.shippingFee
                current.copy(
                    appliedPromoCode = trimmed,
                    discountAmount = discount,
                    totalAmount = total
                )
            }
            return true
        }
        return false
    }

    fun removePromoCode() {
        _uiState.update { current ->
            val total = if (current.subtotal == 0.0) 0.0 else current.subtotal + current.shippingFee
            current.copy(
                appliedPromoCode = null,
                discountAmount = 0.0,
                totalAmount = total
            )
        }
    }

    fun updateUserAddress(newAddress: ShippingAddress) {
        val updated = _uiState.value.userProfile.copy(defaultAddress = newAddress)
        _uiState.update { it.copy(userProfile = updated) }
        viewModelScope.launch {
            authRepository.updateUserProfile(updated)
        }
    }

    fun placeOrder(shippingAddress: ShippingAddress): Order? {
        val currentItems = _uiState.value.cartItems
        if (currentItems.isEmpty()) return null

        val subtotal = _uiState.value.subtotal
        val shipping = _uiState.value.shippingFee
        val discount = _uiState.value.discountAmount

        val order = orderRepository.createOrder(
            items = currentItems,
            subtotal = subtotal,
            shipping = shipping,
            shippingAddress = shippingAddress,
            discount = discount,
            paymentMethod = "Credit Card"
        )
        cartRepository.clearCart()
        _uiState.update { it.copy(lastPlacedOrder = order, appliedPromoCode = null, discountAmount = 0.0) }
        return order
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    // Authentication methods
    fun signIn(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = authRepository.signInWithEmail(email, pass)
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }

    fun signUp(name: String, email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = authRepository.signUpWithEmail(name, email, pass)
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }

    fun continueAsGuest(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            authRepository.continueAsGuest()
            onSuccess()
        }
    }

    fun signOut(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            authRepository.signOut()
            onSuccess()
        }
    }

    fun clearAuthError() {
        authRepository.clearError()
        _uiState.update { it.copy(authError = null) }
    }

    // Backwards-compatible methods for existing callers/tests
    fun login(email: String, pass: String): Boolean {
        if (email.isNotBlank() && pass.isNotBlank()) {
            viewModelScope.launch {
                authRepository.signInWithEmail(email, pass)
            }
            return true
        }
        return false
    }

    fun signUp(name: String, email: String, pass: String): Boolean {
        if (name.isNotBlank() && email.isNotBlank() && pass.isNotBlank()) {
            viewModelScope.launch {
                authRepository.signUpWithEmail(name, email, pass)
            }
            return true
        }
        return false
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
