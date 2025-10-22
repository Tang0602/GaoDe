package com.example.GaoDe.ui.home

import com.example.GaoDe.data.DataManager
import com.example.GaoDe.model.PlaceDetails
import com.example.GaoDe.model.Review
import kotlinx.coroutines.*

class ShowPlaceDetailsPresenter(
    private val view: ShowPlaceDetailsContract.View,
    private val dataManager: DataManager
) : ShowPlaceDetailsContract.Presenter {
    
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    override fun start() {
        // Presenter started
    }
    
    override fun stop() {
        presenterScope.cancel()
    }
    
    override fun loadPlaceDetails(placeId: String) {
        view.showLoading()
        
        presenterScope.launch {
            try {
                val places = dataManager.getPlaces()
                val place = places.find { it.id == placeId }
                
                if (place != null) {
                    // Create mock reviews for demonstration
                    val mockReviews = listOf(
                        Review(
                            userId = "user_fB40918",
                            userName = "用户_fB40918",
                            rating = 5.0f,
                            comment = "好像营业到凌晨3:00, 我快2:00到的服务员很热情",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    
                    val placeDetails = PlaceDetails(
                        place = place,
                        businessHours = "周一至周日 00:00-24:00",
                        facilities = listOf("街道口火锅榜", "食材很新鲜", "可订大厅", "汤味道浓郁", "服务好"),
                        reviews = mockReviews,
                        photos = listOf("friend_1.jpg", "friend_2.jpg", "friend_3.jpg")
                    )
                    
                    view.hideLoading()
                    view.showPlaceDetails(placeDetails)
                } else {
                    view.hideLoading()
                    view.showError("地点信息未找到")
                }
            } catch (e: Exception) {
                view.hideLoading()
                view.showError("加载失败: ${e.message}")
            }
        }
    }
}