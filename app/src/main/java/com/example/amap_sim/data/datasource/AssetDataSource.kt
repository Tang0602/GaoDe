package com.example.amap_sim.data.datasource

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AssetDataSource(private val context: Context) {
    private val gson = Gson()

    fun <T> loadFromAssets(fileName: String, typeToken: TypeToken<T>): T {
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        return gson.fromJson(jsonString, typeToken.type)
    }
}
