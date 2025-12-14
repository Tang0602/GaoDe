package com.example.amap_sim.data.repository

import com.example.amap_sim.data.datasource.AssetDataSource
import com.example.amap_sim.model.UserPreferences
import com.google.gson.reflect.TypeToken

class UserPreferencesRepository(private val assetDataSource: AssetDataSource) {

    fun getUserPreferences(): List<UserPreferences> {
        return assetDataSource.loadFromAssets("data/user_preferences.json", object : TypeToken<List<UserPreferences>>() {})
    }
}
