package com.example.house_rental.data.model

data class HouseUploadResponse(
    val `data`: Data,
    val message: String,
    val status: Int,
    val success: Boolean
)