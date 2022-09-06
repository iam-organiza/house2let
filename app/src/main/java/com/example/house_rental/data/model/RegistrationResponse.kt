package com.example.house_rental.data.model

data class RegistrationResponse(
    val protocol: String,
    val code: Int,
    val message: String,
    val url: String
)