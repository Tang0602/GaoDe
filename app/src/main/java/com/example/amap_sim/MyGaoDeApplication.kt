package com.example.amap_sim

import android.app.Application
import android.os.Build
import android.util.Log
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.CoordType
import org.json.JSONArray
import java.io.File

class MyGaoDeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d("MyGaoDeApplication", "========== 应用初始化 ==========")
        Log.d("MyGaoDeApplication", "Android 版本: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})")
        Log.d("MyGaoDeApplication", "设备型号: ${Build.MODEL}")

        // 【新增】创建百度地图的自定义存储目录（使用应用专属目录，无需存储权限）
        val mapDataDir = getExternalFilesDir("BaiduMapSDK")?.absolutePath
            ?: (filesDir.absolutePath + "/BaiduMapSDK")

        Log.d("MyGaoDeApplication", "百度地图数据目录: $mapDataDir")
        val dirCreated = File(mapDataDir).mkdirs()
        Log.d("MyGaoDeApplication", "目录创建结果: $dirCreated (false表示已存在)")

        // 百度地图隐私政策设置（必须在初始化之前调用）
        SDKInitializer.setAgreePrivacy(this, true)

        // 初始化百度地图SDK
        SDKInitializer.initialize(this)
        SDKInitializer.setCoordType(CoordType.BD09LL)

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
            "19_hotel_booking_history.json",
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