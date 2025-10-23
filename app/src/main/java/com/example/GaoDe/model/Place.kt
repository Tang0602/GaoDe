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

data class POIItem(
    val id: String,
    val brandName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val phone: String? = null,
    val rating: Float,
    val ratingText: String,
    val pricePerPerson: String,
    val viewCount: String,
    val logo: String,
    val verified: Boolean = true,
    val operatingStatus: String,
    val certificationTags: List<String> = emptyList(),
    val distance: String,
    val travelTime: String,
    val rankingInfo: String? = null,
    val specialties: List<String> = emptyList(),
    val userQuote: String? = null,
    val promotionInfo: String? = null,
    val groupBuyInfo: GroupBuyInfo? = null,
    val actionButtonText: String = "订单"
)

data class GroupBuyInfo(
    val currentPrice: String,
    val originalPrice: String,
    val discount: String,
    val description: String
)