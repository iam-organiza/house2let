package com.example.house_rental.data.model

import java.io.Serializable

data class HouseDetailsPayload(val title: String, val address: String, val price: Int, val description: String, val lat: Double = 1.05623, val lng: Double = 1.87654) : Serializable
