package com.example.GaoDe.ui.home

import com.example.GaoDe.base.BasePresenter
import com.example.GaoDe.base.BaseView
import com.example.GaoDe.model.Place
import com.example.GaoDe.model.PlaceDetails

interface ShowPlaceDetailsContract {
    interface View : BaseView {
        fun showPlaceDetails(placeDetails: PlaceDetails)
        fun setPresenter(presenter: Presenter)
    }
    
    interface Presenter {
        fun start()
        fun stop()
        fun loadPlaceDetails(placeId: String)
    }
}