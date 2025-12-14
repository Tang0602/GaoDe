package com.example.amap_sim.ui.home

import com.example.amap_sim.base.BaseView
import com.example.amap_sim.model.Place

interface HomeContract {
    interface View : BaseView {
        fun showNearbyPlaces(places: List<Place>)
        fun showSearchResults(places: List<Place>)
        fun navigateToPlaceDetails(place: Place)
    }
    
    interface Presenter {
        fun loadNearbyPlaces()
        fun searchPlaces(query: String)
        fun onPlaceClicked(place: Place)
        fun onLocationButtonClicked()
        fun onRouteButtonClicked()
    }
}