package com.example.house_rental.data.model

data class GetMeResponse(
    val `data`: DataXXX,
    val message: String,
    val status: Int,
    val success: Boolean
)