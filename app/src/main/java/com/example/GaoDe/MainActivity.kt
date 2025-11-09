package com.example.GaoDe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.amap.api.maps.MapsInitializer
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.GaoDe.ui.home.HomeScreen
import com.example.GaoDe.ui.home.SearchHistoryScreen
import com.example.GaoDe.ui.home.ShowPlaceDetailsScreen
import com.example.GaoDe.ui.home.POIResultsListScreen
import com.example.GaoDe.ui.home.HotelResultsListScreen
import com.example.GaoDe.ui.home.ScenicSpotResultsListScreen
import com.example.GaoDe.ui.home.PlanRouteScreen
import com.example.GaoDe.ui.message.MessageScreen
import com.example.GaoDe.ui.my.MyScreen
import com.example.GaoDe.ui.payment.PaymentSuccessScreen
import com.example.GaoDe.ui.ride.TaxiSuccessScreen
import com.example.GaoDe.ui.ride.RideChatActivity
import com.example.GaoDe.ui.navigation.NavigationSuccessScreen
import com.example.GaoDe.ui.theme.GaoDeTheme
import androidx.compose.ui.unit.dp
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 高德SDK隐私合规初始化
        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)
        
        setContent {
            GaoDeTheme {
                MainScreen()
            }
        }
    }
    
    companion object {
        fun recordNavigationAction(context: Context, action: String, page: String) {
            val fileName = "1_message_history.json"
            val file = File(context.filesDir, fileName)
            
            try {
                val jsonArray = if (file.exists()) {
                    val content = file.readText()
                    if (content.isNotEmpty()) {
                        JSONArray(content)
                    } else {
                        JSONArray()
                    }
                } else {
                    JSONArray()
                }
                
                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val newRecord = JSONObject().apply {
                    put("timestamp", currentTime)
                    put("action", action)
                    put("page", page)
                }
                
                jsonArray.put(newRecord)
                
                file.writeText(jsonArray.toString(2))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

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
                
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
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
                HomeScreen(
                    navController = navController
                )
            }
            composable(Screen.Message.route) {
                MessageScreen()
            }
            composable(Screen.My.route) {
                MyScreen()
            }
            composable(Screen.SearchPlace.route) {
                SearchHistoryScreen(
                    navController = navController,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable("${Screen.ShowPlaceDetails.route}/{placeId}") { backStackEntry ->
                val placeId = backStackEntry.arguments?.getString("placeId") ?: ""
                val context = LocalContext.current
                ShowPlaceDetailsScreen(
                    placeId = placeId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onRouteClick = { placeName ->
                        navController.navigate("${Screen.PlanRoute.route}/$placeName")
                    },
                    onShareClick = { contactId, message ->
                        // 处理分享逻辑
                        val dataManager = com.example.GaoDe.data.DataManager(context)
                        val contactName = if (contactId == "dad") "爸爸" else "妈妈"
                        dataManager.addSharedMessage(contactId, contactName, message)
                    }
                )
            }
            composable("${Screen.POIResultsList.route}/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "美食"
                POIResultsListScreen(
                    searchCategory = category,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onPOIClick = { poiId ->
                        navController.navigate("${Screen.ShowPlaceDetails.route}/$poiId")
                    }
                )
            }
            composable("${Screen.HotelResultsList.route}/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "酒店"
                HotelResultsListScreen(
                    searchCategory = category,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onHotelClick = { hotelId ->
                        navController.navigate("${Screen.ShowPlaceDetails.route}/$hotelId")
                    },
                    onOrderClick = { hotelId ->
                        navController.navigate("${Screen.PaymentSuccess.route}/${Screen.HotelResultsList.route}/$category")
                    }
                )
            }
            composable("${Screen.ScenicSpotResultsList.route}/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "景点"
                ScenicSpotResultsListScreen(
                    searchCategory = category,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onScenicSpotClick = { scenicSpotId ->
                        navController.navigate("${Screen.ShowPlaceDetails.route}/$scenicSpotId")
                    },
                    onOrderClick = { scenicSpotId ->
                        navController.navigate("${Screen.PaymentSuccess.route}/${Screen.ScenicSpotResultsList.route}/$category")
                    }
                )
            }
            composable("${Screen.PlanRoute.route}/{endName}") { backStackEntry ->
                val endName = backStackEntry.arguments?.getString("endName") ?: "目的地"
                PlanRouteScreen(
                    endLocation = endName,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onTaxiClick = {
                        navController.navigate("${Screen.TaxiSuccess.route}/${Screen.PlanRoute.route}/$endName")
                    },
                    onPublicTransportClick = { routeId, startLoc, waypoint, endLoc ->
                        val waypointParam = waypoint?.let { java.net.URLEncoder.encode(it, "UTF-8") } ?: "null"
                        val startParam = java.net.URLEncoder.encode(startLoc, "UTF-8")
                        val endParam = java.net.URLEncoder.encode(endLoc, "UTF-8")
                        navController.navigate("${Screen.NavigationSuccess.route}/$startParam/$waypointParam/$endParam")
                    }
                )
            }
            composable("${Screen.PaymentSuccess.route}/{fromRoute}/{category}") { backStackEntry ->
                val fromRoute = backStackEntry.arguments?.getString("fromRoute") ?: ""
                val category = backStackEntry.arguments?.getString("category") ?: ""
                PaymentSuccessScreen(
                    onConfirmClick = {
                        navController.navigate("$fromRoute/$category") {
                            popUpTo("$fromRoute/$category") {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable("${Screen.TaxiSuccess.route}/{fromRoute}/{endName}") { backStackEntry ->
                val fromRoute = backStackEntry.arguments?.getString("fromRoute") ?: ""
                val endName = backStackEntry.arguments?.getString("endName") ?: ""
                val context = LocalContext.current
                TaxiSuccessScreen(
                    onConfirmClick = {
                        navController.navigate("$fromRoute/$endName") {
                            popUpTo("$fromRoute/$endName") {
                                inclusive = true
                            }
                        }
                    },
                    onContactDriverClick = {
                        // 直接启动打车会话Activity
                        val intent = Intent(context, com.example.GaoDe.ui.ride.RideChatActivity::class.java).apply {
                            putExtra("SESSION_ID", "ride_session_001")
                        }
                        context.startActivity(intent)
                    }
                )
            }
            composable("${Screen.NavigationSuccess.route}/{startLocation}/{waypoint}/{endLocation}") { backStackEntry ->
                val startLocation = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("startLocation") ?: "我的位置", "UTF-8")
                val waypointParam = backStackEntry.arguments?.getString("waypoint") ?: "null"
                val waypoint = if (waypointParam == "null") null else java.net.URLDecoder.decode(waypointParam, "UTF-8")
                val endLocation = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("endLocation") ?: "目的地", "UTF-8")
                
                NavigationSuccessScreen(
                    startLocation = startLocation,
                    waypoint = waypoint,
                    endLocation = endLocation,
                    transportMode = "公共交通",
                    onConfirmClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}