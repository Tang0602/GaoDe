package com.example.GaoDe.model

data class UserPreferences(
    val userId: String,
    val carIcon: CarIcon = CarIcon.DEFAULT,
    val navigationVoice: NavigationVoice = NavigationVoice.STANDARD,
    val mapStyle: MapStyle = MapStyle.STANDARD,
    val routePreference: RoutePreference = RoutePreference.FASTEST,
    val avoidTolls: Boolean = false,
    val avoidHighways: Boolean = false,
    val enableTrafficAlerts: Boolean = true,
    val enableVoiceGuidance: Boolean = true,
    val voiceVolume: Int = 50,
    val nightModeEnabled: Boolean = false
)

enum class CarIcon {
    DEFAULT,
    SEDAN,
    SUV,
    TRUCK,
    MOTORCYCLE,
    BICYCLE
}

enum class NavigationVoice {
    STANDARD,
    MALE_VOICE_1,
    MALE_VOICE_2,
    FEMALE_VOICE_1,
    FEMALE_VOICE_2,
    CELEBRITY_VOICE_1,
    CELEBRITY_VOICE_2
}

enum class MapStyle {
    STANDARD,
    SATELLITE,
    TERRAIN,
    NIGHT_MODE
}

enum class RoutePreference {
    FASTEST,
    SHORTEST,
    AVOID_TRAFFIC,
    SCENIC
}