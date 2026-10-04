package com.example.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val categoryId: String = "",
    val categoryName: String = "",
    val price: Double,
    val originalPrice: Double = price,
    val discountPercentage: Int = if (originalPrice > price && originalPrice > 0) (((originalPrice - price) / originalPrice) * 100).toInt() else 0,
    val rating: Double = 4.5,
    val reviewCount: Int = 0,
    val availableSizes: List<String> = listOf("S", "M", "L"),
    val availableColors: List<ProductColor> = listOf(ProductColor("Classic Black", 0xFF1C1C1E)),
    val stockQuantity: Int = 20,
    val imageUrl: String = "",
    val additionalImageUrls: List<String> = emptyList(),
    val isFeatured: Boolean = false,
    val isPopular: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val category: String = if (categoryName.isNotEmpty()) categoryName else categoryId
) {
    // Secondary constructor to maintain full compatibility with existing code
    constructor(
        id: String,
        name: String,
        description: String,
        price: Double,
        originalPrice: Double,
        discountPercentage: Int,
        imageUrl: String,
        category: String,
        rating: Double,
        reviewCount: Int,
        availableSizes: List<String>,
        availableColors: List<ProductColor>,
        stockQuantity: Int,
        isFeatured: Boolean = false,
        isPopular: Boolean = false
    ) : this(
        id = id,
        name = name,
        description = description,
        categoryId = category.lowercase().replace(" ", "_").replace("-", "_"),
        categoryName = category,
        price = price,
        originalPrice = originalPrice,
        discountPercentage = discountPercentage,
        rating = rating,
        reviewCount = reviewCount,
        availableSizes = availableSizes,
        availableColors = availableColors,
        stockQuantity = stockQuantity,
        imageUrl = imageUrl,
        additionalImageUrls = emptyList(),
        isFeatured = isFeatured,
        isPopular = isPopular,
        createdAt = System.currentTimeMillis(),
        category = category
    )
}

data class ProductColor(
    val name: String,
    val hexCode: Long
)
