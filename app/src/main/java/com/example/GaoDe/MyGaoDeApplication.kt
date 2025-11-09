package com.example.GaoDe

import android.app.Application
import org.json.JSONArray
import java.io.File

class MyGaoDeApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化所有日志文件
        initializeLogFiles()
    }
    
    private fun initializeLogFiles() {
        val allLogFiles = listOf(
            "1_message_history.json",
            "2_favorites_history.json",
            "3_food_search_history.json",
            "4_home_navigation_history.json",
            "5_profile_history.json",
            "6_chat_history.json",
            "7_home_navigation_history.json",
            "8_banu_selection_history.json",
            "9_donghu_ride_history.json",
            "10_hanting_booking_history.json",
            "11_favorite_restaurant_history.json",
            "12_dad_chat_history.json",
            "13_happy_valley_ride_history.json",
            "14_hotel_share_history.json",
            "15_muyu_navigation_history.json",
            "16_skin_history.json",
            "17_navigation_history.json",
            "18_ride_history.json",
            "19_hotel_booking_history.json",
            "20_multi_route_history.json"
        )
        
        // 遍历所有文件，确保它们在App启动时就已经存在
        allLogFiles.forEach { fileName ->
            val file = File(filesDir, fileName)
            if (!file.exists()) {
                try {
                    // 创建文件并写入初始的空JSON数组
                    val initialContent = JSONArray().toString(2)
                    file.writeText(initialContent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}