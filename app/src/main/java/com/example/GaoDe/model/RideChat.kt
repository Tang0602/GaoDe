package com.example.GaoDe.model

data class RideSession(
    val sessionId: String,
    val driverName: String,
    val driverAvatar: String? = null,
    val orderType: String = "实时单",
    val orderId: String,
    val estimatedPrice: String,
    val status: RideStatus,
    val createdAt: Long,
    val lastMessageSummary: String,
    val messages: List<ChatMessage> = emptyList()
)

data class ChatMessage(
    val id: String,
    val type: ChatMessageType,
    val content: String,
    val senderName: String? = null,
    val senderAvatar: String? = null,
    val timestamp: Long,
    val orderId: String? = null,
    val amount: String? = null
)

enum class ChatMessageType {
    SYSTEM_NOTIFICATION,    // 系统提醒
    DRIVER_MESSAGE,         // 司机消息
    PASSENGER_MESSAGE,      // 乘客消息
    ORDER_STATUS           // 订单状态更新
}

enum class RideStatus {
    CREATED("订单已创建"),
    DRIVER_ASSIGNED("司机已接单"),
    DRIVER_ARRIVING("司机即将到达"),
    DRIVER_ARRIVED("司机已到达"),
    TRIP_STARTED("行程开始"),
    TRIP_COMPLETED("行程结束"),
    PAID("已支付");

    val displayName: String

    constructor(displayName: String) {
        this.displayName = displayName
    }
}