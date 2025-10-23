package com.example.GaoDe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.amap.api.maps.MapsInitializer
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
                ShowPlaceDetailsScreen(
                    placeId = placeId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onRouteClick = { placeName ->
                        navController.navigate("${Screen.PlanRoute.route}/$placeName")
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
                    }
                )
            }
            composable("${Screen.PlanRoute.route}/{endName}") { backStackEntry ->
                val endName = backStackEntry.arguments?.getString("endName") ?: "目的地"
                PlanRouteScreen(
                    endLocation = endName,
                    onBackClick = {
                        navController.popBackStack()
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