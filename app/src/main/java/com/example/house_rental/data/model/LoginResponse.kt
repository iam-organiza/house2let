package com.example.house_rental.data.model

data class LoginResponse(
    val data: LoggedUserData,
    val message: String,
    val status: Int,
    val success: Boolean
)