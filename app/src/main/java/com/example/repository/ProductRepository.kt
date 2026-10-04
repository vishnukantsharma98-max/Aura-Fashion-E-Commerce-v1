package com.example.repository

import com.example.data.MockData
import com.example.model.Category
import com.example.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Repository interface for Products and Categories.
 * Designed to easily swap in Firebase Firestore / Cloud Datastore data source.
 */
interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    fun getFeaturedProducts(): Flow<List<Product>>
    fun getPopularProducts(): Flow<List<Product>>
    fun getProductsByCategory(categoryName: String): Flow<List<Product>>
    fun getProductById(productId: String): Flow<Product?>
    fun searchProducts(query: String): Flow<List<Product>>
    fun getCategories(): Flow<List<Category>>
}

class InMemoryProductRepository : ProductRepository {
    private val _products = MutableStateFlow(MockData.products)
    private val _categories = MutableStateFlow(MockData.categories)

    override fun getAllProducts(): Flow<List<Product>> = _products.asStateFlow()

    override fun getFeaturedProducts(): Flow<List<Product>> {
        return _products.map { list -> list.filter { it.isFeatured } }
    }

    override fun getPopularProducts(): Flow<List<Product>> {
        return _products.map { list -> list.filter { it.isPopular } }
    }

    override fun getProductsByCategory(categoryName: String): Flow<List<Product>> {
        return _products.map { list ->
            if (categoryName.equals("All", ignoreCase = true) || categoryName.equals("cat_all", ignoreCase = true)) {
                list
            } else {
                list.filter {
                    it.categoryId.equals(categoryName, ignoreCase = true) ||
                    it.categoryName.equals(categoryName, ignoreCase = true) ||
                    it.category.equals(categoryName, ignoreCase = true)
                }
            }
        }
    }

    override fun getProductById(productId: String): Flow<Product?> {
        return _products.map { list -> list.find { it.id == productId } }
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        return _products.map { list ->
            if (query.isBlank()) {
                list
            } else {
                val clean = query.trim().lowercase()
                list.filter {
                    it.name.lowercase().contains(clean) ||
                        it.categoryName.lowercase().contains(clean) ||
                        it.category.lowercase().contains(clean) ||
                        it.categoryId.lowercase().contains(clean) ||
                        it.description.lowercase().contains(clean)
                }
            }
        }
    }

    override fun getCategories(): Flow<List<Category>> = _categories.asStateFlow()
}
