package com.example.amap_sim.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow

data class HistoryItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val actions: List<String>
)

data class CategoryItem(
    val icon: String,
    val label: String,
    val backgroundColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHistoryScreen(
    navController: androidx.navigation.NavController? = null,
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        // 顶部搜索栏
        TopSearchBar(
            onBackClick = onBackClick,
            onSearchClick = onSearchClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 分类快捷入口
        CategoryShortcutsGrid(navController)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 常用地点标签云
        CommonPlacesChips()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 历史记录
        HistorySection(navController = navController)
    }
}

@Composable
fun TopSearchBar(
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.Gray
                )
            }
            
            Text(
                text = "温泉洗浴",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.7f)
            )
            
            Button(
                onClick = onSearchClick,
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "搜索",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CategoryShortcutsGrid(navController: androidx.navigation.NavController? = null) {
    val categories = listOf(
        CategoryItem("🍽️", "美食", Color(0xFFFFE082)),
        CategoryItem("🏨", "酒店", Color(0xFF90CAF9)),
        CategoryItem("🎫", "景点门票", Color(0xFFA5D6A7)),
        CategoryItem("⛽", "加油充电", Color(0xFFFFAB91)),
        CategoryItem("🎮", "休闲玩乐", Color(0xFFCE93D8)),
        CategoryItem("🛒", "超市", Color(0xFFFFF59D))
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        categories.forEach { category ->
            CategoryShortcutItem(
                category = category,
                modifier = Modifier.weight(1f),
                navController = navController
            )
        }
    }
}

@Composable
fun CategoryShortcutItem(
    category: CategoryItem,
    modifier: Modifier = Modifier,
    navController: androidx.navigation.NavController? = null
) {
    Column(
        modifier = modifier.clickable { 
            if (navController != null) {
                when (category.label) {
                    "美食" -> navController.navigate("POIResultsList/美食")
                    "酒店" -> navController.navigate("HotelResultsList/酒店")
                    "景点门票" -> navController.navigate("ScenicSpotResultsList/景点")
                }
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = category.backgroundColor
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = category.icon,
                    fontSize = 20.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = category.label,
            fontSize = 12.sp,
            color = Color.Black.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun CommonPlacesChips() {
    val commonPlaces = listOf("🏠 家", "🏢 公司", "⭐ 收藏夹")
    
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp
    ) {
        commonPlaces.forEach { place ->
            Surface(
                modifier = Modifier.clickable { },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF5F5F5)
            ) {
                Text(
                    text = place,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun HistorySection(navController: androidx.navigation.NavController? = null) {
    val historyItems = listOf(
        HistoryItem(
            icon = Icons.Default.LocationOn,
            title = "山姆会员商店(光谷店)",
            subtitle = "江夏区",
            actions = listOf("打车", "路线")
        ),
        HistoryItem(
            icon = Icons.Default.LocationOn,
            title = "中影烽禾影城泛悦店",
            subtitle = "洪山区",
            actions = listOf("路线")
        ),
        HistoryItem(
            icon = Icons.Default.Search,
            title = "美食",
            subtitle = "",
            actions = emptyList()
        ),
        HistoryItem(
            icon = Icons.Default.Search,
            title = "酒店",
            subtitle = "",
            actions = emptyList()
        ),
        HistoryItem(
            icon = Icons.Default.Search,
            title = "景点",
            subtitle = "",
            actions = emptyList()
        ),
        HistoryItem(
            icon = Icons.Default.Search,
            title = "电影院",
            subtitle = "",
            actions = emptyList()
        ),
        HistoryItem(
            icon = Icons.Default.LocationOn,
            title = "中国石化南湖北加油站",
            subtitle = "洪山区",
            actions = listOf("路线")
        ),
        HistoryItem(
            icon = Icons.Default.Search,
            title = "黄鹤楼",
            subtitle = "",
            actions = emptyList()
        ),
        HistoryItem(
            icon = Icons.Default.LocationOn,
            title = "武汉光谷广场杨家湾亚朵酒店",
            subtitle = "洪山区",
            actions = listOf("路线")
        ),
        HistoryItem(
            icon = Icons.Default.LocationOn,
            title = "巴奴毛肚火锅(群光广场店)",
            subtitle = "洪山区",
            actions = listOf("路线")
        )
    )
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 历史记录头部
        HistoryHeader()
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 历史记录列表
        historyItems.forEach { item ->
            HistoryRow(
                item = item,
                navController = navController
            )
            if (item != historyItems.last()) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
fun HistoryHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "历史记录",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "清空",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.clickable { }
            )
            Text(
                text = "管理",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.clickable { }
            )
        }
    }
}

@Composable
fun HistoryRow(
    item: HistoryItem,
    navController: androidx.navigation.NavController? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                if (navController != null) {
                    when {
                        item.icon == Icons.Default.LocationOn -> {
                            // Navigate to place details for location items
                            when (item.title) {
                                "巴奴毛肚火锅(群光广场店)" -> {
                                    navController.navigate("ShowPlaceDetails/place_006")
                                }
                                // Add more specific place mappings as needed
                            }
                        }
                        item.icon == Icons.Default.Search -> {
                            // Navigate to POI results for search items
                            when (item.title) {
                                "美食" -> navController.navigate("POIResultsList/美食")
                                "酒店" -> navController.navigate("HotelResultsList/酒店")
                                "景点" -> navController.navigate("ScenicSpotResultsList/景点")
                            }
                        }
                    }
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            if (item.subtitle.isNotEmpty()) {
                Text(
                    text = item.subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        
        if (item.actions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item.actions.forEach { action ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { }
                    ) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = Color(0xFFF5F5F5)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = when (action) {
                                        "打车" -> Icons.Default.DirectionsCar
                                        "路线" -> Icons.Default.Directions
                                        else -> Icons.Default.Place
                                    },
                                    contentDescription = action,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = action,
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}