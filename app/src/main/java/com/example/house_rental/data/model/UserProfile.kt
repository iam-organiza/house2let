package com.example.house_rental.data.model

data class UserProfile(
    val createdAt: String,
    val id: Int,
    val image: String,
    val imageKey: String,
    val incomeSource: String,
    val occupation: String,
    val updatedAt: String,
    val userId: Int
)