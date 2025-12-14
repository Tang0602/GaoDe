package com.example.amap_sim.ui.home

import com.example.amap_sim.base.BasePresenter
import com.example.amap_sim.base.BaseView
import com.example.amap_sim.model.Place
import com.example.amap_sim.model.PlaceDetails

interface ShowPlaceDetailsContract {
    interface View : BaseView {
        fun showPlaceDetails(placeDetails: PlaceDetails)
        fun setPresenter(presenter: Presenter)
    }

    interface Presenter {
        fun start()
        fun stop()
        fun loadPlaceDetails(placeId: String)
        fun getStartLocationName(): String
    }
}