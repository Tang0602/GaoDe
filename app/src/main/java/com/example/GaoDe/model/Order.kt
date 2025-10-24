package com.example.GaoDe.model

import java.util.*

data class Order(
    val id: String,
    val orderType: OrderType,
    val orderTitle: String,
    val status: OrderStatus,
    val price: Double,
    val createdAt: Long,
    val startLocation: String,
    val endLocation: String,
    val isRealTime: Boolean = false
)

enum class OrderType {
    TAXI("打车", "🚖"),
    HOTEL("酒店", "🏨"),
    FUEL("加油", "⛽"),
    CHAUFFEUR("代驾", "🚗"),
    TICKET("门票旅游", "🎫");

    val displayName: String
    val icon: String

    constructor(displayName: String, icon: String) {
        this.displayName = displayName
        this.icon = icon
    }
    
    companion object {
        val ALL: OrderType? = null
    }
}

enum class OrderStatus {
    COMPLETED("已完成"),
    PENDING("进行中"),
    CANCELLED("已取消"),
    REFUNDED("已退款");

    val displayName: String

    constructor(displayName: String) {
        this.displayName = displayName
    }
}

data class OrderSummary(
    val totalCount: Int,
    val completedCount: Int,
    val pendingCount: Int,
    val cancelledCount: Int
)