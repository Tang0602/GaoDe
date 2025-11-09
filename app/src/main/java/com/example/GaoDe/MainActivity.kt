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
import com.example.GaoDe.model.POI
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
        
        // 示例3: 指令4 - 进入主页（生命周期事件）
        recordLog(
            context = this,
            event = LogEvent.HOME_NAVIGATE,
            action = "进入主页",
            page = "主页"
        )
    }
    
    companion object {
        /**
         * 类型安全的日志记录函数
         * 
         * @param context Android上下文
         * @param event 日志事件枚举，包含对应的文件名
         * @param action 用户操作描述
         * @param page 页面名称
         */
        fun recordLog(context: Context, event: LogEvent, action: String, page: String) {
            // 1. 使用一个独特的标签，方便在Logcat中过滤和搜索
            val TAG = "GaoDeLogger_Diagnostic"
            val fileName = event.fileName
            val file = File(context.filesDir, fileName)

            // 2. 打印详细的“入口”日志，告诉我们函数被调用了，以及它的意图是什么
            Log.d(TAG, "========================================")
            Log.d(TAG, "recordLog CALLED for action: '$action'")
            Log.d(TAG, "-> Attempting to write to file: '$fileName'")

            try {
                // 3. 健壮地读取文件内容
                val historyRecords: JSONArray

                if (file.exists()) {
                    val content = file.readText(Charsets.UTF_8)
                    // 关键修复点：如果文件存在但为空，JSONArray(content)会崩溃。我们在这里处理这种情况。
                    if (content.isBlank()) {
                        Log.w(TAG, "-> File '$fileName' exists but is empty. Initializing new JSONArray.")
                        historyRecords = JSONArray()
                    } else {
                        historyRecords = JSONArray(content)
                        Log.d(TAG, "-> File exists and read successfully. Found ${historyRecords.length()} existing records.")
                    }
                } else {
                    // 这是一个警告，因为在我们的架构中，文件应该已经被Application类创建好了
                    Log.w(TAG, "-> WARNING: File '$fileName' does not exist! It should have been pre-created. Creating it now.")
                    historyRecords = JSONArray()
                }

                // 4. 创建新的日志记录 (您的原始逻辑)
                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val newRecord = JSONObject().apply {
                    put("timestamp", currentTime)
                    put("action", action)
                    put("page", page)
                }

                // 5. 追加新记录并写回文件
                historyRecords.put(newRecord)
                Log.d(TAG, "-> New record added. Attempting to write ${historyRecords.length()} total records back to file.")

                file.writeText(historyRecords.toString(2), Charsets.UTF_8)

                Log.d(TAG, "-> SUCCESS: File write operation completed for '$fileName'.")
                Log.d(TAG, "========================================")

            } catch (e: Exception) {
                // 6. !!! 这是最重要的部分：捕获所有未知错误并提供详细信息 !!!
                Log.e(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                Log.e(TAG, "-> FATAL ERROR during log recording for action '$action'.")
                Log.e(TAG, "-> Exception Type: ${e.javaClass.simpleName}")
                Log.e(TAG, "-> Error Message: ${e.message}")
                Log.e(TAG, "-> Stack Trace:", e) // 打印完整的堆栈跟踪，以便深入分析
                Log.e(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            }
        }
        
        // 保持向后兼容的旧函数（已弃用）
        @Deprecated("使用recordLog(context, event, action, page)代替", ReplaceWith("recordLog(context, LogEvent.MESSAGE_PAGE_NAVIGATE, action, page)"))
        fun recordNavigationAction(context: Context, action: String, page: String, fileName: String = "1_message_history.json") {
            val file = File(context.filesDir, fileName)
            
            try {
                val content = file.readText()
                val jsonArray = JSONArray(content)
                
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
            // --- 这是正确的、已修复的代码 ---
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // --- 关键修改：将 context 的获取移动到循环外部但在 Composable 作用域内 ---
                val context = LocalContext.current

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        // --- 这是最终的、已修复的代码 ---
                        onClick = {
                            // 使用 LocalContext.current 来获取Compose中的Context (已移至外部)

                            // 在执行导航之前，先根据被点击的'screen'来记录日志
                            when (screen) {
                                Screen.Home -> {
                                    MainActivity.recordLog(
                                        context = context,
                                        event = LogEvent.HOME_NAVIGATE,
                                        action = "进入主页",
                                        page = "主页"
                                    )
                                }
                                Screen.Message -> {
                                    MainActivity.recordLog(
                                        context = context,
                                        event = LogEvent.MESSAGE_PAGE_NAVIGATE,
                                        action = "点击消息页面",
                                        page = "消息页面"
                                    )
                                }
                                Screen.My -> {
                                    MainActivity.recordLog(
                                        context = context,
                                        event = LogEvent.PROFILE_VIEW,
                                        action = "查看个人中心",
                                        page = "个人中心页面"
                                    )
                                }
                                // !!! 关键修复：添加 else 分支来满足编译器的详尽性检查 !!!
                                else -> {
                                    // 对于其他未知的屏幕类型，我们不执行任何日志记录操作。
                                }
                            }

                            // --- 保持原有的导航逻辑不变 ---
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
                // 1. 在 Composable 的顶层获取 placeId 和 context
                val placeId = backStackEntry.arguments?.getString("placeId") ?: ""
                val context = LocalContext.current

                // 2. 实例化 DataManager，供需要的回调使用
                val dataManager = remember { com.example.GaoDe.data.DataManager(context) }

                // 3. 调用 ShowPlaceDetailsScreen 并提供所有回调的正确实现
                ShowPlaceDetailsScreen(
                    placeId = placeId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onRouteClick = { placeName ->
                        navController.navigate("${Screen.PlanRoute.route}/$placeName")
                    },

                    // --- onShareClick 的实现 ---
                    // 它接收 contactId 和 message 作为参数
                    onShareClick = { contactId, message ->
                        // 只要分享动作发生，就记录日志
                        MainActivity.recordLog(
                            context = context,
                            event = LogEvent.HOTEL_SHARE,
                            action = "分享如家酒店位置给妈妈",
                            page = "地点详情页面"
                        )

                        // 执行分享的业务逻辑 (这里可以使用 contactId 和 message)
                        val contactName = if (contactId == "dad") "爸爸" else "妈妈"
                        dataManager.addSharedMessage(contactId, contactName, message)
                    },

                    // --- onFavoriteClick 的实现 ---
                    // 它接收 currentPlaceId 作为参数
                    onFavoriteClick = { currentPlaceId ->
                        // 只要收藏动作发生，就记录日志
                        MainActivity.recordLog(
                            context = context,
                            event = LogEvent.FAVORITE_RESTAURANT,
                            action = "收藏老乡鸡餐厅",
                            page = "地点详情页面"
                        )
                    }
                )
            }
            composable("${Screen.POIResultsList.route}/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "美食"
                val context = LocalContext.current
                POIResultsListScreen(
                    searchCategory = category,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    // --- 关键修改：回调现在接收一个poi对象 ---
                    onPOIClick = { poi -> // poi 包含了 id 和 name
                        // 指令 #8: 现在我们基于 poi.name 来进行判断
                        if (poi.name.contains("巴奴", ignoreCase = true)) {
                            MainActivity.recordLog(
                                context = context,
                                event = LogEvent.BANU_SELECTION,
                                action = "搜索并选择巴奴火锅",
                                page = "地点详情页面"
                            )
                        }

                        // 导航逻辑使用 poi.id，保持不变
                        navController.navigate("${Screen.ShowPlaceDetails.route}/${poi.id}")
                    }
                )
            }
            composable("${Screen.HotelResultsList.route}/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "酒店"
                val context = LocalContext.current
                HotelResultsListScreen(
                    searchCategory = category,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onHotelClick = { hotelId ->
                        navController.navigate("${Screen.ShowPlaceDetails.route}/$hotelId")
                    },
                    onOrderClick = { hotelId ->
                        // 指令 #10: 预订汉庭酒店
                        MainActivity.recordLog(
                            context = context,
                            event = LogEvent.HANTING_BOOKING,
                            action = "预订汉庭酒店",
                            page = "支付成功页面"
                        )

                        
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
                val context = LocalContext.current
                PlanRouteScreen(
                    endLocation = endName,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onTaxiClick = {
                        // 如果目的地是欢乐谷，记录指令#13，否则都认为是指令#9
                        if (endName.contains("欢乐谷")) {
                            MainActivity.recordLog(
                                context = context,
                                event = LogEvent.HAPPY_VALLEY_RIDE,
                                action = "经济型打车去武汉欢乐谷",
                                page = "打车成功页面"
                            )
                        } else { // 假设其他打车都按指令#9处理
                            MainActivity.recordLog(
                                context = context,
                                event = LogEvent.DONGHU_RIDE,
                                action = "打车去东湖风景区",
                                page = "打车成功页面"
                            )
                        }

                        
                        navController.navigate("${Screen.TaxiSuccess.route}/${Screen.PlanRoute.route}/$endName")
                    },
                    onPublicTransportClick = { routeId, startLoc, waypoint, endLoc ->
                        // 只要点击了任何一条公交路线，就记录日志
                        // 我们测试的是“选择公交路线”这个动作
                        MainActivity.recordLog(
                            context = context,
                            event = LogEvent.MUYU_NAVIGATION,
                            action = "公交导航去木屋烧烤",
                            page = "导航成功页面"
                        )
                        
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