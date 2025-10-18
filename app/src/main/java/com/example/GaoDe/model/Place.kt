package com.example.GaoDe.model

data class Place(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String? = null,
    val phone: String? = null,
    val rating: Float? = null,
    val description: String? = null,
    val imageUrl: String? = null
)

data class PlaceDetails(
    val place: Place,
    val businessHours: String? = null,
    val facilities: List<String> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val photos: List<String> = emptyList()
)

data class Review(
    val userId: String,
    val userName: String,
    val rating: Float,
    val comment: String,
    val timestamp: Long
)