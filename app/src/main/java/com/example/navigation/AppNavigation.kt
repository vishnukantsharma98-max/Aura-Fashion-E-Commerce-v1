package com.example.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.MockData
import com.example.ui.components.AuraBottomNavigation
import com.example.ui.components.AuraTopAppBar
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailsScreen
import com.example.ui.screens.ProductListingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WishlistScreen
import com.example.viewmodel.ShoppingViewModel

@Composable
fun AppNavigation(
    viewModel: ShoppingViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Routes where Bottom Navigation should be displayed
    val bottomNavRoutes = listOf(
        Screen.Home.route,
        Screen.Categories.route,
        Screen.Wishlist.route,
        Screen.Cart.route,
        Screen.Profile.route
    )

    // Routes where top app bar should be displayed
    val showTopBar = currentRoute in bottomNavRoutes || currentRoute?.startsWith("product_listing") == true

    Scaffold(
        topBar = {
            if (showTopBar) {
                val isNotHome = currentRoute != Screen.Home.route
                val title = when {
                    currentRoute == Screen.Categories.route -> "COLLECTIONS"
                    currentRoute == Screen.Wishlist.route -> "MY WISHLIST"
                    currentRoute == Screen.Cart.route -> "SHOPPING BAG"
                    currentRoute == Screen.Profile.route -> "PROFILE"
                    currentRoute?.startsWith("product_listing") == true -> uiState.selectedCategory.uppercase()
                    else -> "A U R A"
                }

                AuraTopAppBar(
                    title = title,
                    showBack = isNotHome && currentRoute !in bottomNavRoutes,
                    showSearch = currentRoute != Screen.Home.route,
                    showCart = currentRoute != Screen.Cart.route,
                    cartItemCount = uiState.cartCount,
                    onBackClick = { navController.popBackStack() },
                    onSearchClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onCartClick = {
                        navController.navigate(Screen.Cart.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                AuraBottomNavigation(
                    currentRoute = currentRoute,
                    cartItemCount = uiState.cartCount,
                    wishlistItemCount = uiState.wishlistIds.size,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route
            ) {
                // 1. Splash Screen
                composable(Screen.Splash.route) {
                    SplashScreen(
                        onSplashFinished = {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }

                // 2. Onboarding Screen
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onFinish = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                // 3. Login Screen
                composable(Screen.Login.route) {
                    LoginScreen(
                        isLoading = uiState.isAuthLoading,
                        errorMessage = uiState.authError,
                        onLoginClick = { email, pass ->
                            viewModel.signIn(email, pass) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        },
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToSignUp = {
                            viewModel.clearAuthError()
                            navController.navigate(Screen.SignUp.route)
                        },
                        onContinueAsGuest = {
                            viewModel.continueAsGuest {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        },
                        onClearError = { viewModel.clearAuthError() }
                    )
                }

                // 4. Sign Up Screen
                composable(Screen.SignUp.route) {
                    SignUpScreen(
                        isLoading = uiState.isAuthLoading,
                        errorMessage = uiState.authError,
                        onSignUpClick = { name, email, pass ->
                            viewModel.signUp(name, email, pass) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.SignUp.route) { inclusive = true }
                                }
                            }
                        },
                        onSignUpSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.SignUp.route) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            viewModel.clearAuthError()
                            navController.popBackStack()
                        },
                        onClearError = { viewModel.clearAuthError() }
                    )
                }

                // 5. Home Screen
                composable(Screen.Home.route) {
                    HomeScreen(
                        uiState = uiState,
                        onCategorySelected = { cat ->
                            viewModel.selectCategory(cat)
                            navController.navigate(Screen.ProductListing.createRoute(cat))
                        },
                        onNavigateToCategories = {
                            navController.navigate(Screen.Categories.route)
                        },
                        onSearchQueryChange = { query ->
                            viewModel.onSearchQueryChange(query)
                        },
                        onProductClick = { product ->
                            viewModel.selectProduct(product)
                            navController.navigate(Screen.ProductDetails.createRoute(product.id))
                        },
                        onWishlistToggle = { product ->
                            viewModel.toggleWishlist(product.id)
                        },
                        onSeeAllCategoryClick = { cat ->
                            viewModel.selectCategory(cat)
                            navController.navigate(Screen.ProductListing.createRoute(cat))
                        },
                        onBannerClick = { categoryTarget ->
                            viewModel.selectCategory(categoryTarget)
                            navController.navigate(Screen.ProductListing.createRoute(categoryTarget))
                        }
                    )
                }

                // 6. Categories Screen
                composable(Screen.Categories.route) {
                    CategoriesScreen(
                        categories = uiState.categories,
                        onCategoryClick = { category ->
                            viewModel.selectCategory(category.name)
                            navController.navigate(Screen.ProductListing.createRoute(category.name))
                        }
                    )
                }

                // 7. Product Listing Screen
                composable(
                    route = Screen.ProductListing.route,
                    arguments = listOf(
                        navArgument("category") {
                            type = NavType.StringType
                            defaultValue = "All"
                        }
                    )
                ) { backStackEntry ->
                    val categoryArg = backStackEntry.arguments?.getString("category") ?: "All"

                    ProductListingScreen(
                        currentCategory = categoryArg,
                        allCategories = uiState.categories,
                        products = uiState.products,
                        wishlistIds = uiState.wishlistIds,
                        onCategorySelected = { cat ->
                            viewModel.selectCategory(cat)
                        },
                        onProductClick = { product ->
                            viewModel.selectProduct(product)
                            navController.navigate(Screen.ProductDetails.createRoute(product.id))
                        },
                        onWishlistToggle = { product ->
                            viewModel.toggleWishlist(product.id)
                        }
                    )
                }

                // 8. Product Details Screen
                composable(
                    route = Screen.ProductDetails.route,
                    arguments = listOf(
                        navArgument("productId") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId") ?: ""
                    val currentProduct = uiState.products.find { it.id == productId }
                        ?: MockData.products.find { it.id == productId }
                        ?: uiState.selectedProduct

                    ProductDetailsScreen(
                        product = currentProduct,
                        isWishlisted = uiState.wishlistIds.contains(productId),
                        cartItemCount = uiState.cartCount,
                        onBackClick = { navController.popBackStack() },
                        onWishlistToggle = { prod -> viewModel.toggleWishlist(prod.id) },
                        onNavigateToWishlist = {
                            navController.navigate(Screen.Wishlist.route)
                        },
                        onAddToCart = { prod, size, color, qty ->
                            viewModel.addToCart(prod, size, color, qty)
                        },
                        onGoToCart = {
                            navController.navigate(Screen.Cart.route)
                        }
                    )
                }

                // 9. Wishlist Screen
                composable(Screen.Wishlist.route) {
                    WishlistScreen(
                        wishlistProducts = uiState.wishlistProducts,
                        wishlistIds = uiState.wishlistIds,
                        onProductClick = { product ->
                            viewModel.selectProduct(product)
                            navController.navigate(Screen.ProductDetails.createRoute(product.id))
                        },
                        onWishlistToggle = { product ->
                            viewModel.toggleWishlist(product.id)
                        },
                        onExploreShop = {
                            navController.navigate(Screen.Home.route)
                        }
                    )
                }

                // 10. Cart Screen
                composable(Screen.Cart.route) {
                    CartScreen(
                        cartItems = uiState.cartItems,
                        subtotal = uiState.subtotal,
                        discountAmount = uiState.discountAmount,
                        appliedPromoCode = uiState.appliedPromoCode,
                        shippingFee = uiState.shippingFee,
                        totalAmount = uiState.totalAmount,
                        onUpdateQuantity = { id, qty ->
                            viewModel.updateCartQuantity(id, qty)
                        },
                        onRemoveItem = { id ->
                            viewModel.removeFromCart(id)
                        },
                        onApplyPromoCode = { code ->
                            viewModel.applyPromoCode(code)
                        },
                        onRemovePromoCode = {
                            viewModel.removePromoCode()
                        },
                        onProceedToCheckout = {
                            navController.navigate(Screen.Checkout.route)
                        },
                        onContinueShopping = {
                            navController.navigate(Screen.Home.route)
                        }
                    )
                }

                // 11. Checkout Screen
                composable(Screen.Checkout.route) {
                    CheckoutScreen(
                        cartItems = uiState.cartItems,
                        subtotal = uiState.subtotal,
                        discountAmount = uiState.discountAmount,
                        shippingFee = uiState.shippingFee,
                        totalAmount = uiState.totalAmount,
                        defaultAddress = uiState.userProfile.defaultAddress,
                        onBackClick = { navController.popBackStack() },
                        onUpdateAddress = { newAddr ->
                            viewModel.updateUserAddress(newAddr)
                        },
                        onPlaceOrder = { address ->
                            viewModel.placeOrder(address)
                        },
                        onOrderCompleteNavToOrders = {
                            navController.navigate(Screen.Orders.route) {
                                popUpTo(Screen.Cart.route) { inclusive = true }
                            }
                        }
                    )
                }

                // 12. Orders Screen
                composable(Screen.Orders.route) {
                    OrdersScreen(
                        orders = uiState.orders,
                        onBackClick = { navController.popBackStack() },
                        onShopNowClick = {
                            navController.navigate(Screen.Home.route)
                        }
                    )
                }

                // 13. Profile Screen
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        userProfile = uiState.userProfile,
                        orderCount = uiState.orders.size,
                        wishlistCount = uiState.wishlistIds.size,
                        isDarkMode = uiState.isDarkMode,
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                        onNavigateToWishlist = { navController.navigate(Screen.Wishlist.route) },
                        onLogout = {
                            viewModel.signOut {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
