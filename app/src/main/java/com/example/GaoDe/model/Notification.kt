package com.example.GaoDe.model

data class Notification(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val notificationType: NotificationType,
    val isRead: Boolean = false,
    val actionUrl: String? = null,
    val iconUrl: String? = null,
    val priority: NotificationPriority = NotificationPriority.NORMAL
)

enum class NotificationType {
    TRAFFIC_UPDATE,
    ROUTE_SUGGESTION,
    LOCATION_REMINDER,
    SYSTEM_UPDATE,
    EMERGENCY_ALERT,
    GENERAL
}

enum class NotificationPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}