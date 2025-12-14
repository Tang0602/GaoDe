package com.example.amap_sim.data.repository

import com.example.amap_sim.data.datasource.AssetDataSource
import com.example.amap_sim.model.Notification
import com.google.gson.reflect.TypeToken

class NotificationRepository(private val assetDataSource: AssetDataSource) {

    fun getNotifications(): List<Notification> {
        return assetDataSource.loadFromAssets("data/notifications.json", object : TypeToken<List<Notification>>() {})
    }
}
