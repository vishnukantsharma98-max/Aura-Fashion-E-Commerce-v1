package com.example.model

data class Category(
    val id: String,
    val name: String,
    val imageUrl: String,
    val itemCount: Int = 0,
    val description: String = "",
    val displayOrder: Int = 0
)
