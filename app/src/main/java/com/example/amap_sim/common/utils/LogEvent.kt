package com.example.amap_sim

/**
 * 日志事件枚举类
 * 
 * 每个枚举实例代表一个唯一的测试指令（用户操作），
 * 包含对应的日志文件名，确保类型安全和集中管理。
 */
enum class LogEvent(val fileName: String) {
    MESSAGE_PAGE_NAVIGATE("1_message_history.json"),
    FAVORITES_VIEW("2_favorites_history.json"),
    FOOD_SEARCH("3_food_search_history.json"),
    HOME_NAVIGATE("4_home_navigation_history.json"),
    PROFILE_VIEW("5_profile_history.json"),
    MOM_CHAT("6_chat_history.json"),
    HOME_BUTTON_CLICK("7_home_navigation_history.json"),
    BANU_SELECTION("8_banu_selection_history.json"),
    DONGHU_RIDE("9_donghu_ride_history.json"),
    HANTING_BOOKING("10_hanting_booking_history.json"),
    FAVORITE_RESTAURANT("11_favorite_restaurant_history.json"),
    DAD_CHAT("12_dad_chat_history.json"),
    HAPPY_VALLEY_RIDE("13_happy_valley_ride_history.json"),
    HOTEL_SHARE("14_hotel_share_history.json"),
    MUYU_NAVIGATION("15_muyu_navigation_history.json"),



    HOTEL_BOOKING("19_hotel_booking_history.json"),

}