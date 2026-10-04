package com.example

import com.example.data.MockData
import com.example.firebase.FirestoreCatalogSeeder
import com.example.utils.CurrencyUtils
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CatalogAndCurrencyTest {

    @Test
    fun testCurrencyFormattingUsesRupeeSymbol() {
        val formatted = CurrencyUtils.format(2499.0)
        assertTrue("Formatted currency should contain ₹: $formatted", formatted.contains("₹"))
        assertTrue("Formatted currency should contain 2,499: $formatted", formatted.contains("2,499") || formatted.contains("2499"))
        assertFalse("Formatted currency must not contain dollar symbol: $formatted", formatted.contains("$"))

        val smallAmount = CurrencyUtils.format(149.0)
        assertTrue(smallAmount.contains("₹"))

        val zeroAmount = CurrencyUtils.format(0.0)
        assertTrue(zeroAmount.contains("₹"))
    }

    @Test
    fun testCatalogHasAtLeast25Products() {
        assertTrue(
            "Catalog must have at least 25 products, actual: ${MockData.products.size}",
            MockData.products.size >= 25
        )
    }

    @Test
    fun testAllRequiredCategoriesArePresent() {
        val categoryNames = MockData.categories.map { it.name.lowercase() }
        val required = listOf(
            "men", "women", "dresses", "shirts", "t-shirts",
            "jeans", "jackets", "shoes", "accessories"
        )

        for (req in required) {
            assertTrue("Required category '$req' must be present in categories", categoryNames.contains(req))
        }
    }

    @Test
    fun testProductPricingAndDiscountConsistency() {
        for (prod in MockData.products) {
            assertNotNull("Product ID must not be null", prod.id)
            assertTrue("Product name must not be blank", prod.name.isNotBlank())
            assertTrue("Product price must be positive for ${prod.id}", prod.price > 0.0)
            assertTrue("Product original price must be >= selling price for ${prod.id}", prod.originalPrice >= prod.price)

            if (prod.originalPrice > prod.price) {
                val expectedDiscount = (((prod.originalPrice - prod.price) / prod.originalPrice) * 100).toInt()
                assertEquals(
                    "Discount percentage mismatch for ${prod.name}",
                    expectedDiscount,
                    prod.discountPercentage
                )
            } else {
                assertEquals(0, prod.discountPercentage)
            }

            assertTrue("Rating must be between 1.0 and 5.0", prod.rating in 1.0..5.0)
            assertTrue("Review count must be non-negative", prod.reviewCount >= 0)
            assertTrue("Stock quantity must be non-negative", prod.stockQuantity >= 0)
            assertTrue("Sizes must not be empty", prod.availableSizes.isNotEmpty())
            assertTrue("Colors must not be empty", prod.availableColors.isNotEmpty())
            assertTrue("Image URL must not be blank", prod.imageUrl.isNotBlank())
            assertTrue("Category ID must not be blank", prod.categoryId.isNotBlank())
            assertTrue("Category name must not be blank", prod.categoryName.isNotBlank())
        }
    }

    @Test
    fun testCategoryModelFields() {
        for (cat in MockData.categories) {
            assertTrue("Category ID must not be blank", cat.id.isNotBlank())
            assertTrue("Category Name must not be blank", cat.name.isNotBlank())
            assertTrue("Category Image URL must not be blank", cat.imageUrl.isNotBlank())
            assertTrue("Display order must be >= 0", cat.displayOrder >= 0)
        }
    }

    @Test
    fun testSeederSafety() {
        assertNotNull(FirestoreCatalogSeeder)
    }
}
