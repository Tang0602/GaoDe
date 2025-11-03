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
            gson.fromJson(jsonString, object : TypeToken<List<SharedMessage>>() {}.type) ?: getDefaultChatMessages()
        } else {
            getDefaultChatMessages()
        }
    }
    
    private fun getDefaultChatMessages(): List<SharedMessage> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            // 爸爸的聊天记录
            SharedMessage(
                id = "default_dad_1",
                contactId = "dad",
                contactName = "爸爸",
                message = "儿子，今天天气挺好的，记得多喝水",
                timestamp = currentTime - 3600000 * 24 // 1天前
            ),
            SharedMessage(
                id = "default_dad_2", 
                contactId = "dad",
                contactName = "爸爸",
                message = "位置: 华中科技大学东门 - 我在校门口等你",
                timestamp = currentTime - 3600000 * 12 // 12小时前
            ),
            SharedMessage(
                id = "default_dad_3",
                contactId = "dad", 
                contactName = "爸爸",
                message = "工作别太累了，身体最重要",
                timestamp = currentTime - 3600000 * 6 // 6小时前
            ),
            
            // 妈妈的聊天记录
            SharedMessage(
                id = "default_mom_1",
                contactId = "mom",
                contactName = "妈妈", 
                message = "宝贝，妈妈给你做了你最爱吃的红烧肉",
                timestamp = currentTime - 3600000 * 36 // 1.5天前
            ),
            SharedMessage(
                id = "default_mom_2",
                contactId = "mom",
                contactName = "妈妈",
                message = "位置: 南湖花溪公园 - 这里的花开得真漂亮，改天带你来看看",
                timestamp = currentTime - 3600000 * 18 // 18小时前
            ),
            SharedMessage(
                id = "default_mom_3",
                contactId = "mom",
                contactName = "妈妈", 
                message = "记得按时吃饭，不要总是点外卖",
                timestamp = currentTime - 3600000 * 8 // 8小时前
            ),
            SharedMessage(
                id = "default_mom_4",
                contactId = "mom",
                contactName = "妈妈",
                message = "周末回家吃饭吗？妈妈给你炖汤喝",
                timestamp = currentTime - 3600000 * 2 // 2小时前
            )
        )
    }
    
    fun addSharedMessage(contactId: String, contactName: String, message: String): Boolean {
        val existingMessages = if (sharedMessagesFile.exists()) {
            val jsonString = sharedMessagesFile.readText()
            gson.fromJson(jsonString, object : TypeToken<List<SharedMessage>>() {}.type) ?: emptyList<SharedMessage>()
        } else {
            emptyList<SharedMessage>()
        }
        
        val newMessage = SharedMessage(
            id = "shared_${System.currentTimeMillis()}",
            contactId = contactId,
            contactName = contactName,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        
        // 只保存用户新增的消息到文件
        val userMessages = existingMessages.toMutableList()
        userMessages.add(0, newMessage)
        
        val jsonString = gson.toJson(userMessages)
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