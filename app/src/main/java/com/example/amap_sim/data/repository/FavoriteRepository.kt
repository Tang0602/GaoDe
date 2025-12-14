package com.example.amap_sim.data.repository

import com.example.amap_sim.data.datasource.AssetDataSource
import com.example.amap_sim.data.datasource.LocalStorageDataSource
import com.example.amap_sim.model.Favorite
import com.example.amap_sim.model.FavoriteType
import com.example.amap_sim.model.Place
import com.google.gson.reflect.TypeToken

class FavoriteRepository(
    private val assetDataSource: AssetDataSource,
    private val localStorageDataSource: LocalStorageDataSource
) {
    private val favoritesFileName = "favorites.json"

    fun getFavorites(): List<Favorite> {
        return if (localStorageDataSource.fileExists(favoritesFileName)) {
            // Read from user's local file
            localStorageDataSource.readFromFile(
                favoritesFileName,
                object : TypeToken<List<Favorite>>() {}
            ) ?: loadDefaultFavorites()
        } else {
            // Load default from assets
            loadDefaultFavorites()
        }
    }

    private fun loadDefaultFavorites(): List<Favorite> {
        return assetDataSource.loadFromAssets("data/favorites.json", object : TypeToken<List<Favorite>>() {})
    }

    fun saveFavorites(favorites: List<Favorite>) {
        localStorageDataSource.writeToFile(favoritesFileName, favorites)
    }

    fun isPlaceFavorited(placeId: String): Boolean {
        return getFavorites().any { it.place.id == placeId }
    }

    fun addToFavorites(place: Place): Boolean {
        val favorites = getFavorites().toMutableList()

        // Check if already favorited
        if (favorites.any { it.place.id == place.id }) {
            return false
        }

        // Create new favorite
        val newFavorite = Favorite(
            id = "fav_${System.currentTimeMillis()}",
            userId = "user_001", // Default user
            place = place,
            favoriteType = FavoriteType.PLACE,
            customName = null,
            notes = null,
            createdAt = System.currentTimeMillis(),
            lastAccessed = System.currentTimeMillis(),
            accessCount = 1
        )

        favorites.add(newFavorite)
        saveFavorites(favorites)
        return true
    }

    fun removeFromFavorites(placeId: String): Boolean {
        val favorites = getFavorites().toMutableList()
        val originalSize = favorites.size

        favorites.removeAll { it.place.id == placeId }

        if (favorites.size < originalSize) {
            saveFavorites(favorites)
            return true
        }
        return false
    }
}
