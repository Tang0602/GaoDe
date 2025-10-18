package com.example.GaoDe.data

import android.content.Context
import com.example.GaoDe.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataManager(private val context: Context) {
    private val gson = Gson()
    
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
        return loadFromAssets("data/favorites.json", object : TypeToken<List<Favorite>>() {})
    }
    
    fun getRoutes(): List<Route> {
        return loadFromAssets("data/routes.json", object : TypeToken<List<Route>>() {})
    }
    
    private fun <T> loadFromAssets(fileName: String, typeToken: TypeToken<T>): T {
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        return gson.fromJson(jsonString, typeToken.type)
    }
}