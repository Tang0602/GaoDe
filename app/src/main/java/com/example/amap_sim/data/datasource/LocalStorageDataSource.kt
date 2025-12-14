package com.example.amap_sim.data.datasource

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class LocalStorageDataSource(private val context: Context) {
    private val gson = Gson()
    private val prettyGson = GsonBuilder().setPrettyPrinting().create()

    fun <T> readFromFile(fileName: String, typeToken: TypeToken<T>): T? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            val jsonString = file.readText()
            gson.fromJson(jsonString, typeToken.type)
        } else {
            null
        }
    }

    fun <T> writeToFile(fileName: String, data: T, pretty: Boolean = false) {
        val file = File(context.filesDir, fileName)
        val jsonString = if (pretty) {
            prettyGson.toJson(data)
        } else {
            gson.toJson(data)
        }
        file.writeText(jsonString)
    }

    fun fileExists(fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return file.exists()
    }
}
