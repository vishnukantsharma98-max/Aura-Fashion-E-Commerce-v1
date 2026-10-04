package com.example

import com.example.data.MockData
import com.example.model.ProductColor
import com.example.model.ShippingAddress
import com.example.repository.FirebaseStorageRepository
import com.example.repository.InMemoryAuthRepository
import com.example.repository.InMemoryCartRepository
import com.example.repository.InMemoryOrderRepository
import com.example.repository.InMemoryProductRepository
import com.example.repository.InMemoryWishlistRepository
import com.example.viewmodel.ShoppingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ShoppingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val authRepo = InMemoryAuthRepository(MockData.sampleUser)
        val prodRepo = InMemoryProductRepository()
        val cartRepo = InMemoryCartRepository()
        val wishRepo = InMemoryWishlistRepository(prodRepo)
        val orderRepo = InMemoryOrderRepository()
        val storageRepo = FirebaseStorageRepository()

        viewModel = ShoppingViewModel(
            authRepository = authRepo,
            productRepository = prodRepo,
            cartRepository = cartRepo,
            wishlistRepository = wishRepo,
            orderRepository = orderRepo,
            storageRepository = storageRepo
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateLoaded() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.products.isEmpty())
        assertFalse(state.categories.isEmpty())
        assertTrue(state.cartItems.isNotEmpty())
        assertTrue(state.cartCount > 0)
        assertTrue(state.totalAmount > 0.0)
    }

    @Test
    fun testAddToCartUpdatesBadgeAndTotals() = runTest {
        advanceUntilIdle()
        val initialCount = viewModel.uiState.value.cartCount
        val testProduct = MockData.products.first()
        val testColor = testProduct.availableColors.first()

        viewModel.addToCart(testProduct, "M", testColor, 2)
        advanceUntilIdle()

        val updatedState = viewModel.uiState.value
        assertEquals(initialCount + 2, updatedState.cartCount)
        assertTrue(updatedState.subtotal > 0.0)
    }

    @Test
    fun testUpdateQuantityAndRemoveItem() = runTest {
        advanceUntilIdle()
        val firstItem = viewModel.uiState.value.cartItems.first()

        // Increase quantity
        viewModel.updateCartQuantity(firstItem.id, firstItem.quantity + 1)
        advanceUntilIdle()
        assertEquals(
            firstItem.quantity + 1,
            viewModel.uiState.value.cartItems.first { it.id == firstItem.id }.quantity
        )

        // Decrease to 0 removes item
        viewModel.updateCartQuantity(firstItem.id, 0)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.cartItems.any { it.id == firstItem.id })
    }

    @Test
    fun testWishlistToggle() = runTest {
        advanceUntilIdle()
        val productId = "prod_3"
        val initiallyWishlisted = viewModel.isWishlisted(productId)

        viewModel.toggleWishlist(productId)
        advanceUntilIdle()
        assertEquals(!initiallyWishlisted, viewModel.uiState.value.wishlistIds.contains(productId))

        // Toggle back
        viewModel.toggleWishlist(productId)
        advanceUntilIdle()
        assertEquals(initiallyWishlisted, viewModel.uiState.value.wishlistIds.contains(productId))
    }

    @Test
    fun testSearchQueryFiltersProducts() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("Cashmere")
        val state = viewModel.uiState.value
        assertTrue(state.filteredProducts.all {
            it.name.contains("Cashmere", ignoreCase = true) ||
            it.description.contains("Cashmere", ignoreCase = true)
        })

        viewModel.onSearchQueryChange("")
        assertEquals(state.products.size, viewModel.uiState.value.filteredProducts.size)
    }

    @Test
    fun testCategoryFiltering() = runTest {
        advanceUntilIdle()
        viewModel.selectCategory("Jackets")
        val state = viewModel.uiState.value
        assertEquals("Jackets", state.selectedCategory)
        assertTrue(state.filteredProducts.isNotEmpty())
        assertTrue(state.filteredProducts.all {
            it.categoryId.equals("cat_jackets", ignoreCase = true) ||
            it.categoryName.equals("Jackets", ignoreCase = true) ||
            it.category.equals("Jackets", ignoreCase = true)
        })
    }

    @Test
    fun testPromoCodeCalculation() = runTest {
        advanceUntilIdle()
        val success = viewModel.applyPromoCode("AURA10")
        assertTrue(success)

        val state = viewModel.uiState.value
        assertEquals("AURA10", state.appliedPromoCode)
        assertTrue(state.discountAmount > 0.0)

        // Invalid code
        val failure = viewModel.applyPromoCode("INVALID_CODE")
        assertFalse(failure)

        // Remove code
        viewModel.removePromoCode()
        assertEquals(null, viewModel.uiState.value.appliedPromoCode)
        assertEquals(0.0, viewModel.uiState.value.discountAmount, 0.001)
    }

    @Test
    fun testPlaceOrderClearsCartAndCreatesOrder() = runTest {
        advanceUntilIdle()
        val address = ShippingAddress(
            fullName = "Lady Victoria",
            street = "10 Downing Street",
            city = "London",
            state = "UK",
            zipCode = "SW1A 2AA",
            phone = "+44 20 7925 0918"
        )

        val order = viewModel.placeOrder(address)
        advanceUntilIdle()

        assertNotNull(order)
        assertEquals(0, viewModel.uiState.value.cartCount)
        assertTrue(viewModel.uiState.value.cartItems.isEmpty())
        assertEquals(order, viewModel.uiState.value.lastPlacedOrder)
    }

    @Test
    fun testToggleDarkMode() = runTest {
        val initialDark = viewModel.uiState.value.isDarkMode
        viewModel.toggleDarkMode()
        assertEquals(!initialDark, viewModel.uiState.value.isDarkMode)
    }

    @Test
    fun testSignInAndGuestFlow() = runTest {
        advanceUntilIdle()

        // Test Guest mode
        viewModel.continueAsGuest()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isGuestUser)
        assertEquals("Guest Client", viewModel.uiState.value.userProfile.name)

        // Test Sign In
        viewModel.signIn("client@aurafashion.com", "pass1234")
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isGuestUser)
        assertTrue(viewModel.uiState.value.isLoggedIn)

        // Test Sign Out
        viewModel.signOut()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun testSignUpValidation() = runTest {
        advanceUntilIdle()

        // Invalid short password
        viewModel.signUp("Victoria", "vic@example.com", "123")
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.authError)

        // Clear error
        viewModel.clearAuthError()
        assertEquals(null, viewModel.uiState.value.authError)
    }
}
