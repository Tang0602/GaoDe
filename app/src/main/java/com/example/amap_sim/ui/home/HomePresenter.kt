package com.example.amap_sim.ui.home

import com.example.amap_sim.base.BasePresenter
import com.example.amap_sim.data.repository.PlaceRepository
import com.example.amap_sim.model.Place

class HomePresenter(private val placeRepository: PlaceRepository) :
    BasePresenter<HomeContract.View>(), HomeContract.Presenter {

    override fun loadNearbyPlaces() {
        if (!isViewAttached()) return

        view?.showLoading()

        try {
            val places = placeRepository.getPlaces()
            view?.showNearbyPlaces(places)
        } catch (e: Exception) {
            view?.showError("加载附近地点失败: ${e.message}")
        } finally {
            view?.hideLoading()
        }
    }

    override fun searchPlaces(query: String) {
        if (!isViewAttached() || query.isBlank()) return

        try {
            val allPlaces = placeRepository.getPlaces()
            val filteredPlaces = allPlaces.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.address.contains(query, ignoreCase = true) ||
                it.category?.contains(query, ignoreCase = true) == true
            }
            view?.showSearchResults(filteredPlaces)
        } catch (e: Exception) {
            view?.showError("搜索失败: ${e.message}")
        }
    }
    
    override fun onPlaceClicked(place: Place) {
        if (!isViewAttached()) return
        view?.navigateToPlaceDetails(place)
    }
    
    override fun onLocationButtonClicked() {
        loadNearbyPlaces()
    }
    
    override fun onRouteButtonClicked() {
        
    }
}