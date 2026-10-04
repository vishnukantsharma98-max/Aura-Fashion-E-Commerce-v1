package com.example.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String,
    val membershipTier: String = "Gold Member",
    val defaultAddress: ShippingAddress,
    val createdAt: Long = System.currentTimeMillis(),
    val isGuest: Boolean = false,
    val profileImageUrl: String = avatarUrl
)
