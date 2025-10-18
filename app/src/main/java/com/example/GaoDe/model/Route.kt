package com.example.GaoDe.model

data class Route(
    val id: String,
    val startPoint: Place,
    val endPoint: Place,
    val waypoints: List<Place> = emptyList(),
    val routePoints: List<RoutePoint>,
    val distance: Double,
    val estimatedTime: Long,
    val routeType: RouteType = RouteType.DRIVING,
    val traffic: TrafficLevel = TrafficLevel.NORMAL,
    val instructions: List<NavigationInstruction> = emptyList()
)

data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double? = null
)

data class NavigationInstruction(
    val id: String,
    val instruction: String,
    val distance: Double,
    val duration: Long,
    val turnDirection: TurnDirection,
    val roadName: String? = null
)

enum class RouteType {
    DRIVING,
    WALKING,
    CYCLING,
    PUBLIC_TRANSPORT
}

enum class TrafficLevel {
    LIGHT,
    NORMAL,
    HEAVY,
    SEVERE
}

enum class TurnDirection {
    STRAIGHT,
    LEFT,
    RIGHT,
    SLIGHT_LEFT,
    SLIGHT_RIGHT,
    SHARP_LEFT,
    SHARP_RIGHT,
    U_TURN,
    ROUNDABOUT_LEFT,
    ROUNDABOUT_RIGHT
}