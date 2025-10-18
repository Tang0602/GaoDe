package com.example.GaoDe.ui.home

import com.example.GaoDe.base.BasePresenter
import com.example.GaoDe.data.DataManager
import com.example.GaoDe.model.Place

class HomePresenter(private val dataManager: DataManager) : 
    BasePresenter<HomeContract.View>(), HomeContract.Presenter {
    
    override fun loadNearbyPlaces() {
        if (!isViewAttached()) return
        
        view?.showLoading()
        
        try {
            val places = dataManager.getPlaces()
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
            val allPlaces = dataManager.getPlaces()
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