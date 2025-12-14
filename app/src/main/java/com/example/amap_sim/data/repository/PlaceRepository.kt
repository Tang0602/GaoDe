package com.example.amap_sim.data.repository

import com.example.amap_sim.data.datasource.AssetDataSource
import com.example.amap_sim.model.Place
import com.google.gson.reflect.TypeToken

class PlaceRepository(private val assetDataSource: AssetDataSource) {

    fun getPlaces(): List<Place> {
        return assetDataSource.loadFromAssets("data/places.json", object : TypeToken<List<Place>>() {})
    }
}
