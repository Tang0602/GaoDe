package com.example.GaoDe.data

import android.content.Context
import com.example.GaoDe.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class DataManager(private val context: Context) {
    private val gson = Gson()
    private val favoritesFile = File(context.filesDir, "favorites.json")
    private val sharedMessagesFile = File(context.filesDir, "shared_messages.json")
    
    fun getPlaces(): List<Place> {
        return loadFromAssets("data/places.json", object : TypeToken<List<Place>>() {})
    }
    
    fun getMessages(): List<Message> {
        return loadFromAssets("data/messages.json", object : TypeToken<List<Message>>() {})
    }
    
    fun getNotifications(): List<Notification> {
        return loadFromAssets("data/notifications.json", object : TypeToken<List<Notification>>() {})
    }
    
    fun getUserPreferences(): List<UserPreferences> {
        return loadFromAssets("data/user_preferences.json", object : TypeToken<List<UserPreferences>>() {})
    }
    
    fun getFavorites(): List<Favorite> {
        return if (favoritesFile.exists()) {
            // Read from user's local file
            val jsonString = favoritesFile.readText()
            gson.fromJson(jsonString, object : TypeToken<List<Favorite>>() {}.type)
        } else {
            // Load default from assets
            loadFromAssets("data/favorites.json", object : TypeToken<List<Favorite>>() {})
        }
    }
    
    fun saveFavorites(favorites: List<Favorite>) {
        val jsonString = gson.toJson(favorites)
        favoritesFile.writeText(jsonString)
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
    
    fun getRoutes(): List<Route> {
        return loadFromAssets("data/routes.json", object : TypeToken<List<Route>>() {})
    }
    
    // 分享消息管理
    data class SharedMessage(
        val id: String,
        val contactId: String,
        val contactName: String,
        val message: String,
        val timestamp: Long
    )
    
    fun getSharedMessages(): List<SharedMessage> {
        return if (sharedMessagesFile.exists()) {
            val jsonString = sharedMessagesFile.readText()
            gson.fromJson(jsonString, object : TypeToken<List<SharedMessage>>() {}.type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun addSharedMessage(contactId: String, contactName: String, message: String): Boolean {
        val messages = getSharedMessages().toMutableList()
        val newMessage = SharedMessage(
            id = "shared_${System.currentTimeMillis()}",
            contactId = contactId,
            contactName = contactName,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        messages.add(0, newMessage) // 添加到列表开头
        
        val jsonString = gson.toJson(messages)
        sharedMessagesFile.writeText(jsonString)
        return true
    }
    
    fun getMessagesForContact(contactId: String): List<SharedMessage> {
        return getSharedMessages().filter { it.contactId == contactId }
    }
    
    private fun <T> loadFromAssets(fileName: String, typeToken: TypeToken<T>): T {
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        return gson.fromJson(jsonString, typeToken.type)
    }
}