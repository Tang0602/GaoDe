package com.example.amap_sim.ui.home

import android.content.Context
// 高德SDK导入已注释 - 待迁移到百度SDK
// import com.amap.api.location.AMapLocation
// import com.amap.api.location.AMapLocationClient
// import com.amap.api.location.AMapLocationClientOption
// import com.amap.api.location.AMapLocationListener
// import com.amap.api.services.core.LatLonPoint
// import com.amap.api.services.poisearch.PoiResult
// import com.amap.api.services.poisearch.PoiSearch
import com.example.amap_sim.data.repository.PlaceRepository
import com.example.amap_sim.model.Place
import com.example.amap_sim.model.PlaceDetails
import com.example.amap_sim.model.Review
import kotlinx.coroutines.*
import android.util.Log

// 简单的位置数据类，替代高德SDK的LatLonPoint
data class SimpleLocation(
    val latitude: Double,
    val longitude: Double
)

class ShowPlaceDetailsPresenter(
    private val view: ShowPlaceDetailsContract.View,
    private val placeRepository: PlaceRepository,
    private val context: Context? = null
) : ShowPlaceDetailsContract.Presenter /* , PoiSearch.OnPoiSearchListener, AMapLocationListener */ {

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    // private var poiSearch: PoiSearch? = null // 高德SDK - 已注释
    // private var mLocationClient: AMapLocationClient? = null // 高德SDK - 已注释

    // 固定起点（华中师范大学南湖校区）
    private var startLocationName: String = "华中师范大学（南湖校区）"
    private val fixedStartLat = 30.519752
    private val fixedStartLon = 114.357138

    override fun start() {
        Log.d("GaoDeTest", "【初始化】ShowPlaceDetailsPresenter.start() 方法被调用。")
        // 高德SDK初始化已注释 - 待迁移到百度SDK
    }

    override fun stop() {
        presenterScope.cancel()
        // 高德SDK资源释放已注释
    }

    override fun loadPlaceDetails(placeId: String) {
        view.showLoading()
        // 所有POI使用本地数据（高德SDK查询已注释）
        loadLocalPlaceDetails(placeId)
    }

    override fun getStartLocationName(): String {
        return startLocationName
    }

    // 获取用户位置（返回固定坐标）
    fun getUserLocation(): SimpleLocation {
        return SimpleLocation(fixedStartLat, fixedStartLon)
    }

    // 加载本地POI数据
    private fun loadLocalPlaceDetails(placeId: String) {
        presenterScope.launch {
            try {
                val place = withContext(Dispatchers.IO) {
                    placeRepository.getPlaces().find { it.id == placeId }
                }

                if (place != null) {
                    val placeDetails = PlaceDetails(
                        place = place
                        // facilities 和 reviews 使用默认值（空列表）
                    )

                    view.hideLoading()
                    view.showPlaceDetails(placeDetails)
                } else {
                    view.hideLoading()
                    view.showError("未找到地点信息")
                }
            } catch (e: Exception) {
                view.hideLoading()
                view.showError("加载地点信息失败: ${e.message}")
            }
        }
    }

    // 以下所有高德SDK相关方法已注释 - 待迁移到百度SDK

    /*
    private fun searchPoiByKeywordForDetails(keyword: String) {
        // 高德POI搜索已注释
    }

    private fun searchPoiDetailById(poiId: String) {
        // 高德POI详情查询已注释
    }

    override fun onPoiSearched(result: PoiResult?, errorCode: Int) {
        // 高德POI搜索回调已注释
    }

    override fun onPoiItemSearched(item: com.amap.api.services.poisearch.PoiItem?, errorCode: Int) {
        // 高德POI详情回调已注释
    }

    override fun onLocationChanged(location: AMapLocation?) {
        // 高德定位回调已注释
    }
    */
}
