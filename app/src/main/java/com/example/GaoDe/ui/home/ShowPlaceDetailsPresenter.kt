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
                facilities = listOf("V认证", "夜宵必备", "小腰", "羔羊肉串", "可订位"),
                reviews = listOf(
                    Review(
                        userId = "user_muwo123",
                        userName = "用户_muwo123",
                        rating = 5.0f,
                        comment = "晚上宵夜常来，烧烤很正宗，氛围感拉满。",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf()
            )
            
            "place_008" -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 00:00-24:00",
                facilities = listOf("V认证", "儿童友好", "24小时营业", "免费停车"),
                reviews = listOf(
                    Review(
                        userId = "user_mcd456",
                        userName = "用户_mcd456",
                        rating = 4.0f,
                        comment = "适合带孩子来，玩具和餐点都很好。",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf()
            )
            
            "place_011" -> PlaceDetails(
                place = place,
                businessHours = "全天接待",
                facilities = listOf("经济型", "免费WiFi", "24小时前台", "华科大学区", "性价比高"),
                reviews = listOf(
                    Review(
                        userId = "user_ht789",
                        userName = "用户_ht789",
                        rating = 4.0f,
                        comment = "性价比很高，环境整洁，适合学生家长入住。",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf()
            )
            
            "place_012" -> PlaceDetails(
                place = place,
                businessHours = "全天接待",
                facilities = listOf("度假型", "近地铁", "湖景房", "温泉SPA", "免费停车", "儿童乐园"),
                reviews = listOf(
                    Review(
                        userId = "user_jl456",
                        userName = "用户_jl456",
                        rating = 5.0f,
                        comment = "环境优美，服务一流，温泉设施很棒，适合度假。",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf()
            )
            
            "place_013" -> PlaceDetails(
                place = place,
                businessHours = "全天接待",
                facilities = listOf("经济型", "近地铁", "交通便利", "免费WiFi", "接送服务"),
                reviews = listOf(
                    Review(
                        userId = "user_rj101",
                        userName = "用户_rj101",
                        rating = 4.0f,
                        comment = "交通方便，适合中转住宿，房间虽小但设施齐全。",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf()
            )
            
            "place_014" -> PlaceDetails(
                place = place,
                businessHours = "全天接待",
                facilities = listOf("奢华型", "近地铁", "行政酒廊", "健身中心", "室内泳池", "米其林餐厅"),
                reviews = listOf(
                    Review(
                        userId = "user_hyatt202",
                        userName = "用户_hyatt202",
                        rating = 5.0f,
                        comment = "奢华体验，服务无可挑剔，商务设施一流。",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf()
            )
            
            "place_015" -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 09:00-22:00",
                facilities = listOf("4A景区", "官方售票", "周边游", "亲子户外", "武汉市游乐园榜 第1名"),
                reviews = listOf(
                    Review(
                        userId = "user_hv303",
                        userName = "用户_hv303",
                        rating = 5.0f,
                        comment = "设施齐全，游乐项目丰富，适合全家出游。",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf()
            )
            
            "place_016" -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 06:00-18:00",
                facilities = listOf("红色景点", "免费参观", "红色教育", "自然风光", "武汉市自然风光榜 第2名"),
                reviews = listOf(
                    Review(
                        userId = "user_dh404",
                        userName = "用户_dh404",
                        rating = 5.0f,
                        comment = "风景优美，湖光山色，是武汉市民休闲的好去处。",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf()
            )
            
            "place_017" -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 08:00-18:00",
                facilities = listOf("3A景区", "官方售票", "文化古迹", "登高望远", "武汉市文化古迹榜 第1名"),
                reviews = listOf(
                    Review(
                        userId = "user_hhl505",
                        userName = "用户_hhl505",
                        rating = 5.0f,
                        comment = "江南三大名楼之一，历史悠久，景色壮观。",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf()
            )
            
            "place_018" -> PlaceDetails(
                place = place,
                businessHours = "全天开放",
                facilities = listOf("生态公园", "免费开放", "生态环境", "休闲漫步", "洪山区生态公园 第1名"),
                reviews = listOf(
                    Review(
                        userId = "user_nh606",
                        userName = "用户_nh606",
                        rating = 4.0f,
                        comment = "环境清幽，空气清新，是晨练和散步的好地方。",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf()
            )
            
            "place_019" -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 09:00-21:00",
                facilities = listOf("4A景区", "官方售票", "海洋世界", "亲子娱乐", "武汉市海洋馆榜 第1名"),
                reviews = listOf(
                    Review(
                        userId = "user_hy707",
                        userName = "用户_hy707",
                        rating = 5.0f,
                        comment = "海洋动物种类丰富，表演精彩，孩子们很喜欢。",
                        timestamp = System.currentTimeMillis()
                    )
                ),
                photos = listOf()
            )
            
            else -> PlaceDetails(
                place = place,
                businessHours = "周一至周日 09:00-21:00",
                facilities = listOf("欢迎咨询", "服务周到"),
                reviews = emptyList(),
                photos = emptyList()
            )
        }
    }
}