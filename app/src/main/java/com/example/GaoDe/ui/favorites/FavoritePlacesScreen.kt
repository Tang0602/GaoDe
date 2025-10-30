package com.example.GaoDe.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.GaoDe.model.Favorite
import com.example.GaoDe.model.FavoriteType
import com.example.GaoDe.model.Place
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import com.example.GaoDe.data.DataManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritePlacesScreen(
    onNavigateBack: () -> Unit = {},
    onFavoriteClick: (Place) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(FavoriteTab.MY_FAVORITES) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部导航栏
        TopAppBar(
            title = {
                Text(
                    text = "我的收藏",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.Black
                    )
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "添加收藏",
                        tint = Color.Black
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )
        
        // 收藏类型筛选栏
        FavoriteTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        // 主要内容区域
        when (selectedTab) {
            FavoriteTab.MY_FAVORITES -> {
                MyFavoritesContent(
                    onFavoriteClick = onFavoriteClick
                )
            }
        }
    }
}

enum class FavoriteTab {
    MY_FAVORITES
}

@Composable
fun FavoriteTabRow(
    selectedTab: FavoriteTab,
    onTabSelected: (FavoriteTab) -> Unit
) {
    TabRow(
        selectedTabIndex = 0,
        containerColor = Color.White,
        contentColor = Color.Blue,
        modifier = Modifier.fillMaxWidth()
    ) {
        Tab(
            selected = selectedTab == FavoriteTab.MY_FAVORITES,
            onClick = { onTabSelected(FavoriteTab.MY_FAVORITES) },
            text = {
                Text(
                    text = "我收藏的",
                    fontWeight = if (selectedTab == FavoriteTab.MY_FAVORITES) FontWeight.Bold else FontWeight.Normal
                )
            }
        )
    }
}

@Composable
fun MyFavoritesContent(
    onFavoriteClick: (Place) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp) // 为底部操作栏留出空间
    ) {
        // 快捷收藏位
        item {
            QuickAccessSection()
        }
        
        // 收藏夹列表
        item {
            FavoritesListSection(
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}

@Composable
fun QuickAccessSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickAccessItem(
                icon = "🏠",
                title = "家",
                modifier = Modifier.weight(1f)
            )
            
            QuickAccessItem(
                icon = "💼",
                title = "公司",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickAccessItem(
    icon: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun FavoritesListSection(
    onFavoriteClick: (Place) -> Unit
) {
    val context = LocalContext.current
    val dataManager = remember { DataManager(context) }
    var favorites by remember { mutableStateOf<List<Favorite>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        favorites = dataManager.getFavorites()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 默认收藏夹标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "默认收藏夹",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Text(
                text = "${favorites.size}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        // 收藏地点列表
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "还没有收藏任何地点",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            favorites.forEachIndexed { index, favorite ->
                FavoritePlaceItem(
                    place = favorite.place,
                    onPlaceClick = { onFavoriteClick(favorite.place) },
                    showActions = index == 1 // 只为第二个项目显示操作按钮
                )
                
                if (index < favorites.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color(0xFFF0F0F0),
                        thickness = 1.dp
                    )
                }
            }
        }
        
        // 底部提示
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "没有更多啦",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun FavoritePlaceItem(
    place: Place,
    onPlaceClick: () -> Unit,
    showActions: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlaceClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 定位图标
        Icon(
            Icons.Default.LocationOn,
            contentDescription = "地点",
            tint = Color(0xFF2196F3),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 地点信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = place.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = place.address,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        // 操作按钮
        if (showActions) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "编辑",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// 示例Activity，用于在Android中启动这个Screen
class FavoritePlacesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FavoritePlacesScreen(
                onNavigateBack = {
                    finish()
                },
                onFavoriteClick = { place ->
                    // 处理收藏地点点击事件
                }
            )
        }
    }
}