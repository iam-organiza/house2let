package com.example.house_rental.data.model

data class GetHousesResponse(
    val `data`: DataX,
    val message: String,
    val status: Int,
    val success: Boolean
)