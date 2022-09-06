package com.example.house_rental.data.model

data class User(
    val email: String,
    val firstName: String,
    val id: Int,
    val lastName: String,
    val role: String,
    val refLetter: String,
    val refLetterKey: String
)