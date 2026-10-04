package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.Category
import com.example.model.Product
import com.example.ui.components.CategoryChip
import com.example.ui.components.ProductCard

@Composable
fun ProductListingScreen(
    currentCategory: String,
    allCategories: List<Category>,
    products: List<Product>,
    wishlistIds: Set<String>,
    onCategorySelected: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onWishlistToggle: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember(currentCategory) { mutableStateOf(currentCategory) }
    var sortBy by remember { mutableStateOf("Featured") }

    val filteredProducts = remember(products, selectedCategory) {
        if (selectedCategory.equals("All", ignoreCase = true) || selectedCategory.equals("cat_all", ignoreCase = true)) {
            products
        } else {
            products.filter {
                it.categoryId.equals(selectedCategory, ignoreCase = true) ||
                it.categoryName.equals(selectedCategory, ignoreCase = true) ||
                it.category.equals(selectedCategory, ignoreCase = true)
            }
        }
    }

    val sortedProducts = remember(filteredProducts, sortBy) {
        when (sortBy) {
            "Price: Low to High" -> filteredProducts.sortedBy { it.price }
            "Price: High to Low" -> filteredProducts.sortedByDescending { it.price }
            "Top Rated" -> filteredProducts.sortedByDescending { it.rating }
            else -> filteredProducts
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Category Pills Header
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allCategories) { cat ->
                CategoryChip(
                    name = cat.name,
                    isSelected = selectedCategory.equals(cat.name, ignoreCase = true) || selectedCategory.equals(cat.id, ignoreCase = true),
                    onClick = {
                        selectedCategory = cat.name
                        onCategorySelected(cat.name)
                    }
                )
            }
        }

        // Subheader with Count & Sort Options (LazyRow prevents any text clipping)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${sortedProducts.size} Items",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(start = 8.dp)
            ) {
                val sortOptions = listOf("Featured", "Price: Low to High", "Price: High to Low", "Top Rated")
                items(sortOptions) { option ->
                    val isSelected = sortBy == option
                    val label = when (option) {
                        "Price: Low to High" -> "$ Low-High"
                        "Price: High to Low" -> "$ High-Low"
                        else -> option
                    }
                    CategoryChip(
                        name = label,
                        isSelected = isSelected,
                        onClick = { sortBy = option }
                    )
                }
            }
        }

        if (sortedProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Checkroom,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No Items in \"$selectedCategory\"",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We are curating new items for this department. Browse our other collections in the meantime.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            selectedCategory = "All"
                            onCategorySelected("All")
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("VIEW ALL COLLECTIONS")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sortedProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = wishlistIds.contains(product.id),
                        onProductClick = onProductClick,
                        onWishlistToggle = onWishlistToggle
                    )
                }
            }
        }
    }
}
