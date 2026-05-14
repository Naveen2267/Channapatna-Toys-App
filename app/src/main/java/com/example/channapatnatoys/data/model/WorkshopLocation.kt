package com.example.channapatnatoys.data.model

data class WorkshopLocation(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val distanceKm: Float? = null
)
