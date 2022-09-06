package com.example.house_rental.data.model

data class GetHouseResponse(
    val `data`: House,
    val message: String,
    val status: Int,
    val success: Boolean
)