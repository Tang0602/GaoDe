package com.example.GaoDe.model

data class Place(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String? = null,
    val phone: String? = null,
    val rating: Float? = null,
    val description: String? = null,
    val imageUrl: String? = null
)

data class PlaceDetails(
    val place: Place,
    val businessHours: String? = null,
    val facilities: List<String> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val photos: List<String> = emptyList()
)

data class Review(
    val userId: String,
    val userName: String,
    val rating: Float,
    val comment: String,
    val timestamp: Long
)

data class POIItem(
    val id: String,
    val brandName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val phone: String? = null,
    val rating: Float,
    val ratingText: String,
    val pricePerPerson: String,
    val viewCount: String,
    val logo: String,
    val verified: Boolean = true,
    val operatingStatus: String,
    val certificationTags: List<String> = emptyList(),
    val distance: String,
    val travelTime: String,
    val rankingInfo: String? = null,
    val specialties: List<String> = emptyList(),
    val userQuote: String? = null,
    val promotionInfo: String? = null,
    val groupBuyInfo: GroupBuyInfo? = null,
    val actionButtonText: String = "订单"
)

data class GroupBuyInfo(
    val currentPrice: String,
    val originalPrice: String,
    val discount: String,
    val description: String
)

data class HotelItem(
    val id: String,
    val hotelName: String,
    val hotelType: String, // 如 "高档型"
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val nearMetro: String? = null, // 如 "近地铁"
    val surroundingInfo: String? = null, // 周边信息
    val rating: Float,
    val ratingText: String, // 如 "超棒"
    val rankingInfo: String? = null, // 如 "卓刀泉酒店 第1名"
    val serviceTags: List<String> = emptyList(), // 如 ["免费停车", "近地铁"]
    val userQuote: String? = null,
    val priceInfo: HotelPriceInfo,
    val logo: String? = null,
    val phone: String? = null
)

data class HotelPriceInfo(
    val lowestPriceHint: String, // 如 "过去14天最低价"
    val originalPrice: String? = null, // 划线价格
    val currentPrice: String, // 现价，如 "¥362起"
    val discountInfo: String? = null // 如 "立减 | 优惠 152 ►"
)

data class ScenicSpotItem(
    val id: String,
    val spotName: String,
    val spotLevel: String, // 如 "4A景区", "红色景点", "3A景区", "生态公园"
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distance: String, // 如 "13.1公里"
    val travelTime: String, // 如 "驾车 25分钟"
    val rating: Float,
    val ratingText: String, // 如 "超棒"
    val activityInfo: String, // 如 "近期销量 3508", "5000+ 人去过"
    val rankingInfo: String? = null, // 如 "武汉市游乐园榜 第1名"
    val serviceTags: List<String> = emptyList(), // 如 ["官方售票", "周边游", "亲子户外"]
    val userQuote: String? = null,
    val ticketInfo: TicketInfo? = null, // 门票信息，南湖花溪公园等免费景点为null
    val logo: String? = null,
    val phone: String? = null
)

data class TicketInfo(
    val ticketDescription: String, // 如 "门票-成人票", "夜场门票-单人票"
    val originalPrice: String? = null, // 划线价格，如 "¥230"
    val currentPrice: String // 现价，如 "¥229起"
)