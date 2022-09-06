package com.example.house_rental.data.model

data class Request(
    val House: HouseXX,
    val User: UserXX,
    val createdAt: String,
    val houseId: Int,
    val id: Int,
    val reason: String,
    val status: String,
    val updatedAt: String,
    val userId: Int
)