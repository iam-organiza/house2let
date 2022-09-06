package com.example.house_rental.data.model

data class Transaction(
    val amount: Int,
    val createdAt: String,
    val houseId: Any,
    val id: Int,
    val reason: String,
    val referenceId: String,
    val status: String,
    val type: String,
    val updatedAt: String,
    val userId: Int
)