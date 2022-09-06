package com.example.house_rental.data.model

data class VerifyLoginPayload(
    val email: String,
    val otp: String
)
