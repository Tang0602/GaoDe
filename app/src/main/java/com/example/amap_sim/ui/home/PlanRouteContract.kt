package com.example.amap_sim.ui.home

import com.example.amap_sim.model.RouteOption

interface PlanRouteContract {
    interface View {
        fun showRouteOptions(routes: List<RouteOption>)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun setPresenter(presenter: Presenter)
    }

    interface Presenter {
        fun start()
        fun stop()
        fun searchBusRoute(startLat: Double, startLon: Double, endLat: Double, endLon: Double)
    }
}
