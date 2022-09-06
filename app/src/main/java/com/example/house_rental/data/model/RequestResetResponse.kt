package com.example.house_rental.data.model

data class RequestResetResponse(
    val `data`: List<Any>,
    val message: String,
    val status: Int,
    val success: Boolean
)