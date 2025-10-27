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
                    val placeDetails = createPlaceDetailsForId(place, placeId)
                    
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
    
    private fun createPlaceDetailsForId(place: com.example.GaoDe.model.Place, placeId: String): PlaceDetails {
        return when (placeId) {
            "place_006" -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 00:00-24:00",
                facilities = listOf("街道口火锅榜", "食材很新鲜", "可订大厅", "汤味道浓郁", "服务好"),
                reviews = listOf(
                    Review(
                        userId = "user_fB40918",
                        userName = "用户_fB40918",
                        rating = 5.0f,
                        comment = "好像营业到凌晨3:00, 我快2:00到的服务员很热情",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf("经典毛肚.jpg", "梅花肉.jpg")
            )
            
            "place_007" -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 18:00-2:00",
                facilities = listOf("夜宵必备", "小腰", "羔羊肉串", "可订位", "氛围感拉满"),
                reviews = listOf(
                    Review(
                        userId = "user_muwo123",
                        userName = "用户_muwo123",
                        rating = 5.0f,
                        comment = "晚上宵夜常来，烧烤很正宗，氛围感拉满",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf("烧烤串.jpg", "烤羊肉.jpg")
            )
            
            "place_008" -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 00:00-24:00",
                facilities = listOf("24小时营业", "免费停车", "儿童友好", "快速出餐"),
                reviews = listOf(
                    Review(
                        userId = "user_mcd456",
                        userName = "用户_mcd456",
                        rating = 4.0f,
                        comment = "适合带孩子来，玩具和餐点都很好",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf("汉堡包.jpg", "薯条.jpg")
            )
            
            "place_011" -> PlaceDetails(
                place = place,
                businessHours = "全天",
                facilities = listOf("经济型酒店", "性价比高", "近地铁", "免费WIFI", "24小时前台"),
                reviews = listOf(
                    Review(
                        userId = "user_ht789",
                        userName = "用户_ht789",
                        rating = 4.0f,
                        comment = "房间干净整洁，服务态度好，性价比很高",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf("酒店房间.jpg", "酒店大堂.jpg")
            )
            
            "place_013" -> PlaceDetails(
                place = place,
                businessHours = "全天",
                facilities = listOf("经济型酒店", "交通便利", "近火车站", "免费WIFI", "行李寄存"),
                reviews = listOf(
                    Review(
                        userId = "user_rj101",
                        userName = "用户_rj101",
                        rating = 4.0f,
                        comment = "位置很好找，离火车站很近，出行方便",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf("如家房间.jpg", "如家前台.jpg")
            )
            
            "place_014" -> PlaceDetails(
                place = place,
                businessHours = "全天",
                facilities = listOf("奢华型酒店", "设施一流", "商务中心", "健身房", "游泳池", "行政酒廊"),
                reviews = listOf(
                    Review(
                        userId = "user_hyatt202",
                        userName = "用户_hyatt202",
                        rating = 5.0f,
                        comment = "服务非常专业，房间设施齐全，早餐丰富",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf("凯悦套房.jpg", "凯悦餐厅.jpg")
            )
            
            "place_015" -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 09:00-22:00",
                facilities = listOf("4A级景区", "大型游乐园", "过山车", "摩天轮", "适合全家", "停车场"),
                reviews = listOf(
                    Review(
                        userId = "user_hv303",
                        userName = "用户_hv303",
                        rating = 5.0f,
                        comment = "游乐设施很刺激，带孩子来玩得很开心",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf("过山车.jpg", "摩天轮.jpg")
            )
            
            "place_016" -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 06:00-18:00",
                facilities = listOf("红色景点", "自然风光", "游船", "步道", "摄影胜地", "免费开放"),
                reviews = listOf(
                    Review(
                        userId = "user_dh404",
                        userName = "用户_dh404",
                        rating = 5.0f,
                        comment = "风景优美，空气清新，是休闲散步的好地方",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf("东湖美景.jpg", "湖边小径.jpg")
            )
            
            "place_017" -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 08:00-18:00",
                facilities = listOf("3A级景区", "江南名楼", "历史文化", "观景台", "诗词文化", "登楼眺望"),
                reviews = listOf(
                    Review(
                        userId = "user_hhl505",
                        userName = "用户_hhl505",
                        rating = 5.0f,
                        comment = "登楼远眺长江，感受古人诗词意境，文化底蕴深厚",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf("黄鹤楼外景.jpg", "楼内文物.jpg")
            )
            
            else -> PlaceDetails(
                place = place,
                businessHours = "营业时间请咨询商家",
                facilities = listOf("请联系商家了解详情"),
                reviews = emptyList(),
                photos = emptyList()
            )
        }
    }
}