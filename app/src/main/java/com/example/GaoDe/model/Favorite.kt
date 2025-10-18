package com.example.GaoDe.model

data class Favorite(
    val id: String,
    val userId: String,
    val place: Place,
    val favoriteType: FavoriteType = FavoriteType.PLACE,
    val customName: String? = null,
    val notes: String? = null,
    val createdAt: Long,
    val lastAccessed: Long? = null,
    val accessCount: Int = 0
)

enum class FavoriteType {
    PLACE,
    HOME,
    WORK,
    CUSTOM
}

data class FavoriteCollection(
    val id: String,
    val userId: String,
    val name: String,
    val description: String? = null,
    val favorites: List<Favorite> = emptyList(),
    val isDefault: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)