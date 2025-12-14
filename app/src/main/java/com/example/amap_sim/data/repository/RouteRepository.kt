package com.example.amap_sim.data.repository

import com.example.amap_sim.data.datasource.AssetDataSource
import com.example.amap_sim.model.Route
import com.google.gson.reflect.TypeToken

class RouteRepository(private val assetDataSource: AssetDataSource) {

    fun getRoutes(): List<Route> {
        return assetDataSource.loadFromAssets("data/routes.json", object : TypeToken<List<Route>>() {})
    }
}
