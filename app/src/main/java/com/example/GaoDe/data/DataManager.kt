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
        // 获取默认聊天记录（从messages.json加载）
        val defaultMessages = getDefaultChatMessages()
        
        // 获取用户自己发送的消息
        val userMessages = if (sharedMessagesFile.exists()) {
            val jsonString = sharedMessagesFile.readText()
            gson.fromJson<List<SharedMessage>>(jsonString, object : TypeToken<List<SharedMessage>>() {}.type) ?: emptyList<SharedMessage>()
        } else {
            emptyList<SharedMessage>()
        }
        
        // 合并默认消息和用户消息，按时间排序
        return (defaultMessages + userMessages).sortedBy { it.timestamp }
    }
    
    private fun getDefaultChatMessages(): List<SharedMessage> {
        // 从messages.json中加载与爸妈的聊天记录
        val allMessages = getMessages()
        val parentMessages = allMessages.filter { message ->
            message.senderId == "dad" || message.senderId == "mom" || 
            message.receiverId == "dad" || message.receiverId == "mom"
        }.map { message ->
            val contactId = when {
                message.senderId == "dad" || message.receiverId == "dad" -> "dad"
                message.senderId == "mom" || message.receiverId == "mom" -> "mom"
                else -> "unknown"
            }
            val contactName = when (contactId) {
                "dad" -> "爸爸"
                "mom" -> "妈妈"
                else -> "未知联系人"
            }
            
            SharedMessage(
                id = message.id,
                contactId = contactId,
                contactName = contactName,
                message = message.content,
                timestamp = message.timestamp
            )
        }
        
        return parentMessages.ifEmpty {
            // 如果没有找到爸妈的消息，使用默认数据
            val currentTime = System.currentTimeMillis()
            listOf<SharedMessage>(
                SharedMessage(
                    id = "default_dad_1",
                    contactId = "dad",
                    contactName = "爸爸",
                    message = "儿子，今天天气挺好的，记得多喝水",
                    timestamp = currentTime - 3600000 * 24
                ),
                SharedMessage(
                    id = "default_mom_1",
                    contactId = "mom",
                    contactName = "妈妈",
                    message = "宝贝，记得按时吃饭",
                    timestamp = currentTime - 3600000 * 12
                )
            )
        }
    }
    
    fun addSharedMessage(contactId: String, contactName: String, message: String): Boolean {
        val existingMessages = if (sharedMessagesFile.exists()) {
            val jsonString = sharedMessagesFile.readText()
            gson.fromJson<List<SharedMessage>>(jsonString, object : TypeToken<List<SharedMessage>>() {}.type) ?: emptyList<SharedMessage>()
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