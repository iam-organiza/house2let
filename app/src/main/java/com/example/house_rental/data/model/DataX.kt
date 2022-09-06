package com.example.house_rental.data.model

data class DataX(
    val currentPage: Int,
    val items: List<House>,
    val totalItems: Int,
    val totalPages: Int
)