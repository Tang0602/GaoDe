package com.example.GaoDe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.amap.api.maps.MapsInitializer
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.GaoDe.model.Hotel
import com.example.GaoDe.model.POI
import com.example.GaoDe.ui.home.*
import com.example.GaoDe.ui.message.MessageScreen
import com.example.GaoDe.ui.my.MyScreen
import com.example.GaoDe.ui.navigation.NavigationSuccessScreen
import com.example.GaoDe.ui.payment.PaymentSuccessScreen
import com.example.GaoDe.ui.ride.TaxiSuccessScreen
import com.example.GaoDe.ui.theme.GaoDeTheme

// MainActivity and companion object with recordLog function (No changes here, so it is kept as is)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)

        setContent {
            GaoDeTheme {
                MainScreen()
            }
        }

        recordLog(
            context = this,
            event = LogEvent.HOME_NAVIGATE,
            action = "进入主页",
            page = "主页"
        )
    }

    companion object {
        fun recordLog(context: Context, event: LogEvent, action: String, page: String) {
            val TAG = "GaoDeLogger_Diagnostic"
            val fileName = event.fileName
            val file = File(context.filesDir, fileName)
            Log.d(TAG, "========================================")
            Log.d(TAG, "recordLog CALLED for action: '$action'")
            Log.d(TAG, "-> Attempting to write to file: '$fileName'")
            try {
                val historyRecords: JSONArray
                if (file.exists()) {
                    val content = file.readText(Charsets.UTF_8)
                    if (content.isBlank()) {
                        Log.w(TAG, "-> File '$fileName' exists but is empty. Initializing new JSONArray.")
                        historyRecords = JSONArray()
                    } else {
                        historyRecords = JSONArray(content)
                        Log.d(TAG, "-> File exists and read successfully. Found ${historyRecords.length()} existing records.")
                    }
                } else {
                    Log.w(TAG, "-> WARNING: File '$fileName' does not exist! It should have been pre-created. Creating it now.")
                    historyRecords = JSONArray()
                }
                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val newRecord = JSONObject().apply {
                    put("timestamp", currentTime)
                    put("action", action)
                    put("page", page)
                }
                historyRecords.put(newRecord)
                Log.d(TAG, "-> New record added. Attempting to write ${historyRecords.length()} total records back to file.")
                file.writeText(historyRecords.toString(2), Charsets.UTF_8)
                Log.d(TAG, "-> SUCCESS: File write operation completed for '$fileName'.")
                Log.d(TAG, "========================================")
            } catch (e: Exception) {
                Log.e(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                Log.e(TAG, "-> FATAL ERROR during log recording for action '$action'.")
                Log.e(TAG, "-> Exception Type: ${e.javaClass.simpleName}")
                Log.e(TAG, "-> Error Message: ${e.message}")
                Log.e(TAG, "-> Stack Trace:", e)
                Log.e(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            }
        }
    }
}

// Sealed class Screen (Removed SkinStore as it's part of the rolled-back features)
sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home : Screen("home", Icons.Filled.Home, "首页")
    object Message : Screen("message", Icons.Filled.Mail, "消息")
    object My : Screen("my", Icons.Filled.AccountCircle, "我的")
    object SearchPlace : Screen("SearchPlace", Icons.Filled.Search, "搜索地点")
    object ShowPlaceDetails : Screen("ShowPlaceDetails", Icons.Filled.Place, "地点详情")
    object POIResultsList : Screen("POIResultsList", Icons.Filled.Search, "POI结果列表")
    object HotelResultsList : Screen("HotelResultsList", Icons.Filled.Place, "酒店结果列表")
    object ScenicSpotResultsList : Screen("ScenicSpotResultsList", Icons.Filled.Place, "景点结果列表")
    object PlanRoute : Screen("PlanRoute", Icons.Filled.Place, "路线规划")
    object PaymentSuccess : Screen("PaymentSuccess", Icons.Filled.AccountCircle, "支付成功")
    object TaxiSuccess : Screen("TaxiSuccess", Icons.Filled.AccountCircle, "打车成功")
    object NavigationSuccess : Screen("NavigationSuccess", Icons.Filled.AccountCircle, "导航成功")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Message, Screen.My)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val context = LocalContext.current

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            when (screen) {
                                Screen.Home -> {
                                    MainActivity.recordLog(context = context, event = LogEvent.HOME_NAVIGATE, action = "进入主页", page = "主页")
                                }
                                Screen.Message -> {
                                    MainActivity.recordLog(context = context, event = LogEvent.MESSAGE_PAGE_NAVIGATE, action = "点击消息页面", page = "消息页面")
                                }
                                Screen.My -> {
                                    MainActivity.recordLog(context = context, event = LogEvent.PROFILE_VIEW, action = "查看个人中心", page = "个人中心页面")
                                }
                                else -> { }
                            }
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            selectedIconColor = androidx.compose.ui.graphics.Color(0xFF2196F3),
                            selectedTextColor = androidx.compose.ui.graphics.Color(0xFF2196F3),
                            unselectedIconColor = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.6f),
                            unselectedTextColor = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Message.route) {
                MessageScreen()
            }
            composable(Screen.My.route) {
                // Removed onSkinClick as SkinStore route is removed
                MyScreen(onSkinClick = {})
            }
            // Removed composable(Screen.SkinStore.route)
            composable(Screen.SearchPlace.route) {
                SearchHistoryScreen(navController = navController, onBackClick = { navController.popBackStack() })
            }
            composable("${Screen.ShowPlaceDetails.route}/{placeId}") { backStackEntry ->
                val placeId = backStackEntry.arguments?.getString("placeId") ?: ""
                val context = LocalContext.current
                val dataManager = remember { com.example.GaoDe.data.DataManager(context) }

                ShowPlaceDetailsScreen(
                    placeId = placeId,
                    onBackClick = { navController.popBackStack() },
                    onRouteClick = { startLat, startLon, endLat, endLon, placeName ->
                        navController.navigate("${Screen.PlanRoute.route}/$placeName/$startLat/$startLon/$endLat/$endLon")
                    },
                    onShareClick = { contactId, message ->
                        MainActivity.recordLog(context = context, event = LogEvent.HOTEL_SHARE, action = "分享如家酒店位置给妈妈", page = "地点详情页面")
                        val contactName = if (contactId == "dad") "爸爸" else "妈妈"
                        dataManager.addSharedMessage(contactId, contactName, message)
                    },
                    onFavoriteClick = { currentPlaceId ->
                        MainActivity.recordLog(context = context, event = LogEvent.FAVORITE_RESTAURANT, action = "收藏老乡鸡餐厅", page = "地点详情页面")
                    }
                )
            }
            composable("${Screen.POIResultsList.route}/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "美食"
                val context = LocalContext.current
                POIResultsListScreen(
                    searchCategory = category,
                    onBackClick = { navController.popBackStack() },
                    onPOIClick = { poi ->
                        if (poi.name.contains("巴奴", ignoreCase = true)) {
                            MainActivity.recordLog(context = context, event = LogEvent.BANU_SELECTION, action = "搜索并选择巴奴火锅", page = "地点详情页面")
                        }
                        navController.navigate("${Screen.ShowPlaceDetails.route}/${poi.id}")
                    }
                )
            }
            composable("${Screen.HotelResultsList.route}/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "酒店"
                val context = LocalContext.current
                HotelResultsListScreen(
                    searchCategory = category,
                    onBackClick = { navController.popBackStack() },
                    onHotelClick = { hotelId -> navController.navigate("${Screen.ShowPlaceDetails.route}/$hotelId") },
                    onOrderClick = { hotel ->
                        if (hotel.name.contains("汉庭", ignoreCase = true)) {
                            MainActivity.recordLog(context = context, event = LogEvent.HANTING_BOOKING, action = "预订汉庭酒店", page = "支付成功页面")
                        }
                        // Kept instruction #19 logic
                        if (hotel.name.contains("凯悦", ignoreCase = true)) {
                            MainActivity.recordLog(context = context, event = LogEvent.HOTEL_BOOKING, action = "查找并预订最贵的酒店", page = "支付成功页面")
                        }
                        navController.navigate("${Screen.PaymentSuccess.route}/${Screen.HotelResultsList.route}/$category")
                    }
                )
            }
            composable("${Screen.ScenicSpotResultsList.route}/{category}") {
                // ... Kept as is ...
                val category = it.arguments?.getString("category") ?: "景点"
                ScenicSpotResultsListScreen(
                    searchCategory = category,
                    onBackClick = { navController.popBackStack() },
                    onScenicSpotClick = { scenicSpotId -> navController.navigate("${Screen.ShowPlaceDetails.route}/$scenicSpotId") },
                    onOrderClick = { scenicSpotId -> navController.navigate("${Screen.PaymentSuccess.route}/${Screen.ScenicSpotResultsList.route}/$category") }
                )

            }
            composable("${Screen.PlanRoute.route}/{endName}/{startLat}/{startLon}/{endLat}/{endLon}") { backStackEntry ->
                val endName = backStackEntry.arguments?.getString("endName") ?: "目的地"
                val startLat = backStackEntry.arguments?.getString("startLat")?.toDoubleOrNull() ?: 30.5167
                val startLon = backStackEntry.arguments?.getString("startLon")?.toDoubleOrNull() ?: 114.4115
                val endLat = backStackEntry.arguments?.getString("endLat")?.toDoubleOrNull() ?: 30.516
                val endLon = backStackEntry.arguments?.getString("endLon")?.toDoubleOrNull() ?: 114.361
                val context = LocalContext.current
                PlanRouteScreen(
                    endLocation = endName,
                    startLat = startLat,
                    startLon = startLon,
                    endLat = endLat,
                    endLon = endLon,
                    onBackClick = { navController.popBackStack() },
                    onTaxiClick = {
                        if (endName.contains("东湖", ignoreCase = true)) {
                            MainActivity.recordLog(context = context, event = LogEvent.DONGHU_RIDE, action = "打车去东湖风景区", page = "打车成功页面")
                        }
                        if (endName.contains("欢乐谷", ignoreCase = true)) {
                            MainActivity.recordLog(context = context, event = LogEvent.HAPPY_VALLEY_RIDE, action = "经济型打车去武汉欢乐谷", page = "打车成功页面")
                        }
                        navController.navigate("${Screen.TaxiSuccess.route}/${Screen.PlanRoute.route}/$endName/$startLat/$startLon/$endLat/$endLon")
                    },
                    // Removed logic for #17 and #20 from onPublicTransportClick and onConfirmMultiRouteClick
                    onPublicTransportClick = { routeId, startLoc, waypoint, endLoc ->
                        Log.d("MyGaoDe_DEBUG_BUS", "Public Transport Route Clicked!")
                        Log.d("MyGaoDe_DEBUG_BUS", "-> routeId: '$routeId'")
                        Log.d("MyGaoDe_DEBUG_BUS", "-> startLoc: '$startLoc'")
                        Log.d("MyGaoDe_DEBUG_BUS", "-> endLoc: '$endLoc'")

                            MainActivity.recordLog(context = context, event = LogEvent.MUYU_NAVIGATION, action = "公交导航去木屋烧烤", page = "导航成功页面")

                        val waypointParam = waypoint?.let { java.net.URLEncoder.encode(it, "UTF-8") } ?: "null"
                        val startParam = java.net.URLEncoder.encode(startLoc, "UTF-8")
                        val endParam = java.net.URLEncoder.encode(endLoc, "UTF-8")
                        navController.navigate("${Screen.NavigationSuccess.route}/$startParam/$waypointParam/$endParam")
                    },
                    // Assuming onConfirmMultiRouteClick was part of PlanRouteScreen, removing the log call
                    onConfirmMultiRouteClick = {
                        // Removed log call for instruction #20
                    }
                )
            }
            composable("${Screen.PaymentSuccess.route}/{fromRoute}/{category}") {
                // ... Kept as is ...
                val fromRoute = it.arguments?.getString("fromRoute") ?: ""
                val category = it.arguments?.getString("category") ?: ""
                PaymentSuccessScreen(onConfirmClick = { navController.navigate("$fromRoute/$category") { popUpTo("$fromRoute/$category") { inclusive = true } } })
            }
            composable("${Screen.TaxiSuccess.route}/{fromRoute}/{endName}/{startLat}/{startLon}/{endLat}/{endLon}") { backStackEntry ->
                // Removed ride chat log call (#18) by removing the intent logic for RideChatActivity
                val fromRoute = backStackEntry.arguments?.getString("fromRoute") ?: ""
                val endName = backStackEntry.arguments?.getString("endName") ?: ""
                val startLat = backStackEntry.arguments?.getString("startLat") ?: "30.5167"
                val startLon = backStackEntry.arguments?.getString("startLon") ?: "114.4115"
                val endLat = backStackEntry.arguments?.getString("endLat") ?: "30.516"
                val endLon = backStackEntry.arguments?.getString("endLon") ?: "114.361"
                val context = LocalContext.current
                TaxiSuccessScreen(
                    onConfirmClick = {
                        navController.navigate("$fromRoute/$endName/$startLat/$startLon/$endLat/$endLon") {
                            popUpTo("$fromRoute/$endName/$startLat/$startLon/$endLat/$endLon") { inclusive = true }
                        }
                    },
                    onContactDriverClick = {
                        // Removed logic for instruction #18
                    }
                )
            }
            composable("${Screen.NavigationSuccess.route}/{startLocation}/{waypoint}/{endLocation}") {
                // ... Kept as is ...
                val startLocation = java.net.URLDecoder.decode(it.arguments?.getString("startLocation") ?: "我的位置", "UTF-8")
                val waypointParam = it.arguments?.getString("waypoint") ?: "null"
                val waypoint = if (waypointParam == "null") null else java.net.URLDecoder.decode(waypointParam, "UTF-8")
                val endLocation = java.net.URLDecoder.decode(it.arguments?.getString("endLocation") ?: "目的地", "UTF-8")
                NavigationSuccessScreen(
                    startLocation = startLocation,
                    waypoint = waypoint,
                    endLocation = endLocation,
                    transportMode = "公共交通",
                    onConfirmClick = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }
                )
            }
        }
    }
}