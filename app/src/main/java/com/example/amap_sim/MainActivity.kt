package com.example.amap_sim

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
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
import com.example.amap_sim.model.Hotel
import com.example.amap_sim.model.POI
import com.example.amap_sim.ui.home.*
import com.example.amap_sim.ui.message.MessageScreen
import com.example.amap_sim.ui.my.MyScreen
import com.example.amap_sim.ui.navigation.NavigationSuccessScreen
import com.example.amap_sim.ui.payment.PaymentSuccessScreen
import com.example.amap_sim.ui.ride.TaxiSuccessScreen
import com.example.amap_sim.ui.theme.GaoDeTheme
// 【新增】导入百度离线地图相关类
import com.baidu.mapapi.map.offline.MKOfflineMap
import com.baidu.mapapi.map.offline.MKOfflineMapListener
import com.baidu.mapapi.map.offline.MKOLSearchRecord
import com.baidu.mapapi.map.offline.MKOLUpdateElement

// MainActivity and companion object with recordLog function (No changes here, so it is kept as is)
class MainActivity : ComponentActivity() {
    // 【修改】声明离线地图管理对象
    private lateinit var mOfflineMap: MKOfflineMap
    // 【新增】标志位：确保初始化逻辑只执行一次
    private var isOfflineMapInitialized = false
    // 【新增】SharedPreferences 用于记录下载状态
    private val prefs: SharedPreferences by lazy {
        getSharedPreferences("offline_map_prefs", Context.MODE_PRIVATE)
    }

    // 【新增】检查是否连接到 WiFi
    private fun isWifiConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return networkInfo?.type == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected
        }
    }

    // 【新增】检查离线地图是否需要下载
    private fun shouldDownloadOfflineMap(cityId: Int): Boolean {
        // 检查是否已经标记为下载完成
        val isDownloaded = prefs.getBoolean("offline_map_wuhan_downloaded", false)
        if (isDownloaded) {
            Log.d("OfflineMapSetup", "SharedPreferences 显示武汉离线地图已下载")
            return false
        }

        // 检查实际下载状态
        val updateInfo = mOfflineMap.getUpdateInfo(cityId)
        if (updateInfo != null && updateInfo.ratio == 100) {
            // 已下载完成，更新 SharedPreferences
            prefs.edit().putBoolean("offline_map_wuhan_downloaded", true).apply()
            Log.d("OfflineMapSetup", "检测到武汉离线地图已完整下载，更新记录")
            return false
        }

        return true
    }

    // 【新增】使用新的 Activity Result API 注册权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val TAG = "PermissionRequest"

        // 检查所有权限是否都被授予
        val allGranted = permissions.entries.all { it.value }
        val deniedPermissions = permissions.entries.filter { !it.value }.map { it.key }

        if (allGranted) {
            // 所有权限都已授予
            Log.d(TAG, "用户已授予所有权限，开始初始化离线地图")
            Toast.makeText(this,
                "存储权限已授予，开始下载离线地图",
                Toast.LENGTH_SHORT).show()
            // 【修改】确保在主线程初始化
            runOnUiThread {
                setupOfflineMap()
            }
        } else {
            // 有权限被拒绝
            Log.e(TAG, "部分权限被拒绝: $deniedPermissions")
            Toast.makeText(this,
                "存储权限被拒绝，离线地图功能将无法使用\n被拒绝的权限: ${deniedPermissions.size} 个",
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        // 【修改】1. 先请求权限
        requestPermissions()

        // 【修改】2. 再初始化离线地图功能（注意：只有在权限已授予时才会真正初始化）
        // setupOfflineMap() 将在权限授予后的回调中调用
    }

    // 【重构】设置百度离线地图的方法
    private fun setupOfflineMap() {
        val TAG = "OfflineMapSetup"

        // 【新增】详细的诊断日志
        Log.d(TAG, "========== 离线地图初始化诊断 ==========")

        // 检查权限状态
        val writeGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        Log.d(TAG, "WRITE_EXTERNAL_STORAGE 权限: ${if (writeGranted == PackageManager.PERMISSION_GRANTED) "✓ 已授予" else "✗ 未授予 (code=$writeGranted)"}")
        Log.d(TAG, "READ_EXTERNAL_STORAGE 权限: ${if (readGranted == PackageManager.PERMISSION_GRANTED) "✓ 已授予" else "✗ 未授予 (code=$readGranted)"}")

        // 检查当前线程
        Log.d(TAG, "当前线程: ${Thread.currentThread().name} (id=${Thread.currentThread().id})")
        Log.d(TAG, "是否为主线程: ${Thread.currentThread() == android.os.Looper.getMainLooper().thread}")

        // 检查 Android 版本
        Log.d(TAG, "Android SDK 版本: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})")

        // 【修改】使用应用专属目录（无需存储权限，Android 12 完全支持）
        val offlineMapDir = getExternalFilesDir("BaiduMapSDK/offlinemap")?.absoluteFile
            ?: File(filesDir, "BaiduMapSDK/offlinemap")

        // 确保目录存在
        if (!offlineMapDir.exists()) {
            val created = offlineMapDir.mkdirs()
            Log.d(TAG, "创建离线地图目录: ${offlineMapDir.absolutePath}, 结果: $created")
        }

        Log.d(TAG, "离线地图存储路径: ${offlineMapDir.absolutePath}")

        try {
            Log.d(TAG, "正在检查目录状态...")
            val exists = offlineMapDir.exists()
            Log.d(TAG, "目录是否存在: $exists")

            val canWrite = offlineMapDir.canWrite()
            Log.d(TAG, "目录是否可写: $canWrite")
        } catch (e: Exception) {
            Log.e(TAG, "检查目录状态时出错: ${e.message}", e)
        }

        Log.d(TAG, "=======================================")

        try {
            // 1. 实例化 MKOfflineMap 对象
            Log.d(TAG, "准备创建 MKOfflineMap 对象...")
            mOfflineMap = MKOfflineMap()
            Log.d(TAG, "✓ MKOfflineMap 对象创建成功")

            // 2. 初始化并设置离线地图监听器（这是一个异步操作）
            Log.d(TAG, "准备调用 mOfflineMap.init()...")
            mOfflineMap.init(object : MKOfflineMapListener {
                /**
                 * 离线地图状态回调
                 * 这个方法会被多次调用，表示不同的状态
                 *
                 * @param type 状态类型：
                 *   - TYPE_DOWNLOAD_UPDATE: 下载进度更新
                 *   - TYPE_NEW_OFFLINE: 新离线地图安装成功
                 *   - TYPE_VER_UPDATE: 离线地图版本更新成功
                 * @param state 当 type 为 TYPE_DOWNLOAD_UPDATE 时，state 表示城市ID
                 */
                override fun onGetOfflineMapState(type: Int, state: Int) {
                    Log.d(TAG, "onGetOfflineMapState 被调用: type=$type, state=$state")

                    // 【新增】立即记录回调触发
                    runOnUiThread {
                        Toast.makeText(this@MainActivity,
                            "✓ 回调已触发! type=$type, state=$state",
                            Toast.LENGTH_SHORT).show()
                    }

                    // 【关键】首次进入回调，执行初始化逻辑（只记录状态，不重复下载）
                    if (!isOfflineMapInitialized) {
                        isOfflineMapInitialized = true
                        Log.d(TAG, "✓ 离线地图回调已激活，开始监控下载进度")

                        // 搜索武汉城市，获取城市ID
                        val searchResult: ArrayList<MKOLSearchRecord> = mOfflineMap.searchCity("武汉")

                        if (searchResult.isNotEmpty()) {
                            val wuhanCityId = searchResult[0].cityID
                            val cityName = searchResult[0].cityName
                            Log.d(TAG, "找到城市: $cityName, ID: $wuhanCityId")

                            // 检查武汉离线地图下载状态
                            val updateInfo: MKOLUpdateElement? = mOfflineMap.getUpdateInfo(wuhanCityId)

                            if (updateInfo != null) {
                                Log.d(TAG, "获取到离线地图信息: serversize=${updateInfo.serversize}, ratio=${updateInfo.ratio}")

                                if (updateInfo.ratio == 100) {
                                    Log.d(TAG, "✓ 武汉离线地图已完整下载")
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity,
                                            "✓ 武汉离线地图已完整下载！",
                                            Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Log.d(TAG, "武汉离线地图下载中... (当前进度: ${updateInfo.ratio}%)")
                                    // 注意：不在这里调用 start()，因为已经在主动测试中启动了下载
                                }
                            } else {
                                Log.e(TAG, "getUpdateInfo 返回 null，无法获取武汉离线地图信息")
                            }
                        } else {
                            Log.e(TAG, "searchCity 未找到武汉城市")
                            runOnUiThread {
                                Toast.makeText(this@MainActivity,
                                    "未找到武汉城市",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // 【后续回调】处理下载进度、完成、错误等状态
                        when (type) {
                            MKOfflineMap.TYPE_DOWNLOAD_UPDATE -> {
                                // 下载进度更新
                                val updateInfo = mOfflineMap.getUpdateInfo(state)
                                if (updateInfo != null) {
                                    val progress = updateInfo.ratio
                                    Log.d(TAG, "武汉离线地图下载进度: $progress%")
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity,
                                            "武汉离线地图下载进度: $progress%",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            MKOfflineMap.TYPE_NEW_OFFLINE -> {
                                // 新离线地图安装成功
                                Log.d(TAG, "✓ 武汉离线地图下载完成！")

                                // 【新增】保存下载完成状态
                                prefs.edit().putBoolean("offline_map_wuhan_downloaded", true).apply()
                                Log.d(TAG, "已更新 SharedPreferences，标记为已下载")

                                runOnUiThread {
                                    Toast.makeText(this@MainActivity,
                                        "✓ 武汉离线地图下载完成！",
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                            MKOfflineMap.TYPE_VER_UPDATE -> {
                                // 离线地图版本更新成功
                                Log.d(TAG, "武汉离线地图更新成功！")
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity,
                                        "武汉离线地图更新成功！",
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                            else -> {
                                // 其他状态（包括错误）
                                Log.w(TAG, "离线地图状态变化: type=$type, state=$state")
                            }
                        }
                    }
                }
            })

            Log.d(TAG, "MKOfflineMap.init() 已调用，等待回调...")

            // 【优化】智能下载逻辑：延迟执行以确保 SDK 初始化完成
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    Log.d(TAG, "========== 检查离线地图状态 ==========")

                    // 1. 获取离线地图列表（必须先调用，否则 getUpdateInfo 返回 null）
                    val allCities = mOfflineMap.offlineCityList
                    Log.d(TAG, "离线地图列表获取成功，共 ${allCities?.size ?: 0} 个城市")

                    // 2. 搜索武汉城市
                    val searchResult = mOfflineMap.searchCity("武汉")
                    if (searchResult.isNullOrEmpty()) {
                        Log.w(TAG, "未找到武汉城市")
                        return@postDelayed
                    }

                    val cityId = searchResult[0].cityID
                    val cityName = searchResult[0].cityName
                    Log.d(TAG, "找到城市: $cityName (ID: $cityId)")

                    // 3. 检查是否需要下载
                    if (!shouldDownloadOfflineMap(cityId)) {
                        Log.d(TAG, "✓ $cityName 离线地图已存在，无需下载")
                        runOnUiThread {
                            Toast.makeText(this@MainActivity,
                                "✓ $cityName 离线地图已存在",
                                Toast.LENGTH_SHORT).show()
                        }
                        return@postDelayed
                    }

                    // 4. 检查网络连接
                    val isWifi = isWifiConnected()
                    Log.d(TAG, "网络状态: ${if (isWifi) "WiFi已连接" else "未连接WiFi"}")

                    if (!isWifi) {
                        Log.w(TAG, "建议在 WiFi 环境下载离线地图")
                        runOnUiThread {
                            Toast.makeText(this@MainActivity,
                                "建议连接 WiFi 后下载离线地图（约 300MB）",
                                Toast.LENGTH_LONG).show()
                        }
                        // 非 WiFi 环境下不自动下载
                        return@postDelayed
                    }

                    // 5. 启动下载
                    Log.d(TAG, "准备下载 $cityName 离线地图...")
                    val started = mOfflineMap.start(cityId)
                    Log.d(TAG, "下载启动结果: $started")

                    if (started) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity,
                                "✓ 开始下载 $cityName 离线地图（约 300MB）",
                                Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Log.e(TAG, "下载启动失败")
                    }

                    Log.d(TAG, "=======================================")
                } catch (e: Exception) {
                    Log.e(TAG, "检查离线地图状态时出错: ${e.message}", e)
                }
            }, 2000) // 延迟 2 秒执行，确保 SDK 完全初始化

        } catch (e: Exception) {
            Log.e(TAG, "离线地图初始化失败", e)
            Toast.makeText(this,
                "离线地图初始化失败: ${e.message}",
                Toast.LENGTH_LONG).show()
        }
    }

    // 【修改】动态权限申请方法（使用新的 Activity Result API）
    private fun requestPermissions() {
        val TAG = "PermissionRequest"

        // Android 6.0 (API 23) 以上需要动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 定义需要申请的权限
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            // 检查哪些权限还未被授予
            val permissionsToRequest = mutableListOf<String>()
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission)
                    Log.d(TAG, "权限未授予: $permission")
                }
            }

            if (permissionsToRequest.isNotEmpty()) {
                // 存在未授予的权限，使用新的 API 发起请求
                Log.d(TAG, "正在请求 ${permissionsToRequest.size} 个权限...")
                requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
            } else {
                // 所有权限已授予
                Log.d(TAG, "所有权限已授予，开始初始化离线地图")
                // 【修改】确保在主线程初始化
                runOnUiThread {
                    setupOfflineMap()
                }
            }
        } else {
            // Android 6.0 以下，权限在安装时已授予
            Log.d(TAG, "Android 版本低于 6.0，无需动态申请权限")
            // 【修改】确保在主线程初始化
            runOnUiThread {
                setupOfflineMap()
            }
        }
    }

    // 【新增】销毁离线地图资源
    override fun onDestroy() {
        super.onDestroy()
        // 释放离线地图资源
        if (::mOfflineMap.isInitialized) {
            mOfflineMap.destroy()
            Log.d("OfflineMapSetup", "离线地图资源已释放")
        }
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
                val assetDataSource = remember { com.example.amap_sim.data.datasource.AssetDataSource(context) }
                val localStorageDataSource = remember { com.example.amap_sim.data.datasource.LocalStorageDataSource(context) }
                val messageRepository = remember { com.example.amap_sim.data.repository.MessageRepository(assetDataSource, localStorageDataSource) }

                ShowPlaceDetailsScreen(
                    placeId = placeId,
                    onBackClick = { navController.popBackStack() },
                    onRouteClick = { startLat, startLon, endLat, endLon, startName, endName ->
                        navController.navigate("${Screen.PlanRoute.route}/$startName/$endName/$startLat/$startLon/$endLat/$endLon")
                    },
                    onShareClick = { contactId, message ->
                        MainActivity.recordLog(context = context, event = LogEvent.HOTEL_SHARE, action = "分享如家酒店位置给妈妈", page = "地点详情页面")
                        val contactName = if (contactId == "dad") "爸爸" else "妈妈"
                        messageRepository.addSharedMessage(contactId, contactName, message)
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
            composable("${Screen.PlanRoute.route}/{startName}/{endName}/{startLat}/{startLon}/{endLat}/{endLon}") { backStackEntry ->
                val startName = backStackEntry.arguments?.getString("startName") ?: "我的位置"
                val endName = backStackEntry.arguments?.getString("endName") ?: "目的地"
                val startLat = backStackEntry.arguments?.getString("startLat")?.toDoubleOrNull() ?: 30.518000
                val startLon = backStackEntry.arguments?.getString("startLon")?.toDoubleOrNull() ?: 114.363000
                val endLat = backStackEntry.arguments?.getString("endLat")?.toDoubleOrNull() ?: 30.516
                val endLon = backStackEntry.arguments?.getString("endLon")?.toDoubleOrNull() ?: 114.361
                val context = LocalContext.current
                PlanRouteScreen(
                    startLocation = startName,
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
                        navController.navigate("${Screen.TaxiSuccess.route}/${Screen.PlanRoute.route}/$startName/$endName/$startLat/$startLon/$endLat/$endLon")
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
            composable("${Screen.TaxiSuccess.route}/{fromRoute}/{startName}/{endName}/{startLat}/{startLon}/{endLat}/{endLon}") { backStackEntry ->
                // Removed ride chat log call (#18) by removing the intent logic for RideChatActivity
                val fromRoute = backStackEntry.arguments?.getString("fromRoute") ?: ""
                val startName = backStackEntry.arguments?.getString("startName") ?: "我的位置"
                val endName = backStackEntry.arguments?.getString("endName") ?: ""
                val startLat = backStackEntry.arguments?.getString("startLat") ?: "30.518000"
                val startLon = backStackEntry.arguments?.getString("startLon") ?: "114.363000"
                val endLat = backStackEntry.arguments?.getString("endLat") ?: "30.516"
                val endLon = backStackEntry.arguments?.getString("endLon") ?: "114.361"
                val context = LocalContext.current
                TaxiSuccessScreen(
                    onConfirmClick = {
                        navController.navigate("$fromRoute/$startName/$endName/$startLat/$startLon/$endLat/$endLon") {
                            popUpTo("$fromRoute/$startName/$endName/$startLat/$startLon/$endLat/$endLon") { inclusive = true }
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