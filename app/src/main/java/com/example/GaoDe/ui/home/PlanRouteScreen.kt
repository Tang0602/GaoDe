package com.example.GaoDe.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.GaoDe.model.RouteOption
import com.example.GaoDe.model.RouteSegment
import com.example.GaoDe.model.TaxiCategory
import com.example.GaoDe.model.TaxiOption
import com.example.GaoDe.model.TaxiGroup
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanRouteScreen(
    startLocation: String = "我的位置",
    endLocation: String = "巴奴毛肚火锅（群光广场店）",
    onBackClick: () -> Unit = {},
    onTaxiClick: () -> Unit = {},
    onPublicTransportClick: (String) -> Unit = {}
) {
    var routeOptions by remember { mutableStateOf<List<RouteOption>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTransportMode by remember { mutableStateOf("公共交通") }
    val context = LocalContext.current

    LaunchedEffect(endLocation) {
        withContext(Dispatchers.IO) {
            try {
                // Generate dynamic route data based on destination
                val items = generateRouteOptions(endLocation)
                
                withContext(Dispatchers.Main) {
                    routeOptions = items
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        PlanRouteTopBar(
            startLocation = startLocation,
            endLocation = endLocation,
            onBackClick = onBackClick
        )
        
        // Transportation Mode Filter
        TransportationModeFilter(
            selectedMode = selectedTransportMode,
            onModeSelected = { selectedTransportMode = it }
        )
        
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Content based on selected transport mode
            if (selectedTransportMode == "打车") {
                TaxiAggregateView(
                    modifier = Modifier.fillMaxSize(),
                    onTaxiClick = onTaxiClick
                )
            } else {
                // Route Options List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(routeOptions) { route ->
                        RouteOptionCard(
                            route = route,
                            onClick = { 
                                when (route.transportationType) {
                                    "打车" -> onTaxiClick()
                                    else -> onPublicTransportClick(route.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlanRouteTopBar(
    startLocation: String,
    endLocation: String,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // First row: Back button and icons
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = "地图",
                            tint = Color.Gray
                        )
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "语音",
                            tint = Color.Gray
                        )
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "清除",
                            tint = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Start and end locations
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // Start location
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = startLocation,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // End location
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFFF5722), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = endLocation,
                            fontSize = 16.sp,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransportationModeFilter(
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    val modes = listOf("打车", "顺风车", "公共交通", "骑行", "步行")
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF8F8F8)
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(modes) { mode ->
                TransportModeChip(
                    mode = mode,
                    isSelected = mode == selectedMode,
                    onClick = { onModeSelected(mode) }
                )
            }
            
            // Extra tabs
            item {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFFF9800)
                ) {
                    Text(
                        text = "新",
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TransportModeChip(
    mode: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = mode,
            fontSize = 16.sp,
            color = if (isSelected) Color(0xFF2196F3) else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(Color(0xFF2196F3))
            )
        }
    }
}


@Composable
fun RouteOptionCard(
    route: RouteOption,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with duration, distance and tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = route.duration,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = route.distance,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Tags
                FlowRow(
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 4.dp
                ) {
                    route.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF5F5F5)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "详情",
                        tint = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Route segments
            RouteSegmentsDisplay(segments = route.mainRoute)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Details
            Text(
                text = route.details,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            // Real-time info
            route.realTimeInfo?.let { info ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = info,
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50)
                )
            }
            
        }
    }
}

@Composable
fun RouteSegmentsDisplay(
    segments: List<RouteSegment>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        segments.forEachIndexed { index, segment ->
            when (segment.type) {
                "步行" -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.DirectionsWalk,
                            contentDescription = "步行",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = segment.description,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                "公交" -> {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF4CAF50)
                    ) {
                        Text(
                            text = segment.description,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                "地铁" -> {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2196F3)
                    ) {
                        Text(
                            text = segment.description,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                "打车" -> {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2196F3)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalTaxi,
                                contentDescription = "打车",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = segment.description,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            if (index < segments.size - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

private fun parseRouteOption(jsonObject: JSONObject): RouteOption {
    val mainRouteArray = jsonObject.getJSONArray("mainRoute")
    val segments = mutableListOf<RouteSegment>()
    
    for (i in 0 until mainRouteArray.length()) {
        val segmentJson = mainRouteArray.getJSONObject(i)
        segments.add(
            RouteSegment(
                type = segmentJson.getString("type"),
                description = segmentJson.getString("description"),
                icon = segmentJson.getString("icon"),
                backgroundColor = segmentJson.optString("backgroundColor").takeIf { it.isNotEmpty() }
            )
        )
    }
    
    val tagsArray = jsonObject.optJSONArray("tags")
    val tags = mutableListOf<String>()
    if (tagsArray != null) {
        for (i in 0 until tagsArray.length()) {
            tags.add(tagsArray.getString(i))
        }
    }
    
    return RouteOption(
        id = jsonObject.getString("id"),
        transportationType = jsonObject.getString("transportationType"),
        duration = jsonObject.getString("duration"),
        distance = jsonObject.getString("distance"),
        price = jsonObject.optString("price").takeIf { it.isNotEmpty() },
        mainRoute = segments,
        details = jsonObject.getString("details"),
        realTimeInfo = jsonObject.optString("realTimeInfo").takeIf { it.isNotEmpty() },
        tags = tags,
        isRecommended = jsonObject.getBoolean("isRecommended")
    )
}

@Composable
fun TaxiAggregateView(
    modifier: Modifier = Modifier,
    onTaxiClick: () -> Unit = {}
) {
    var taxiCategories by remember { mutableStateOf<List<TaxiCategory>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("recommend") }
    var recommendOptions by remember { mutableStateOf<List<TaxiOption>>(emptyList()) }
    var aggregateOptions by remember { mutableStateOf<List<TaxiOption>>(emptyList()) }
    var economyGroup by remember { mutableStateOf<TaxiGroup?>(null) }
    var selectedCount by remember { mutableStateOf(32) }
    
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("data/taxi_options.json")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(jsonString)
                
                // Parse categories
                val categoriesArray = jsonObject.getJSONArray("categories")
                val categories = mutableListOf<TaxiCategory>()
                for (i in 0 until categoriesArray.length()) {
                    val categoryJson = categoriesArray.getJSONObject(i)
                    categories.add(
                        TaxiCategory(
                            id = categoryJson.getString("id"),
                            name = categoryJson.getString("name"),
                            badge = if (categoryJson.isNull("badge")) null else categoryJson.getInt("badge"),
                            isSelected = categoryJson.getBoolean("isSelected")
                        )
                    )
                }
                
                // Parse recommend options
                val recommendArray = jsonObject.getJSONArray("recommendOptions")
                val recommends = mutableListOf<TaxiOption>()
                for (i in 0 until recommendArray.length()) {
                    val optionJson = recommendArray.getJSONObject(i)
                    recommends.add(parseTaxiOption(optionJson))
                }
                
                // Parse aggregate options
                val aggregateArray = jsonObject.getJSONArray("aggregateOptions")
                val aggregates = mutableListOf<TaxiOption>()
                for (i in 0 until aggregateArray.length()) {
                    val optionJson = aggregateArray.getJSONObject(i)
                    aggregates.add(parseTaxiOption(optionJson))
                }
                
                // Parse economy group
                val economyJson = jsonObject.getJSONObject("economyGroup")
                val economyItems = mutableListOf<TaxiOption>()
                val economyItemsArray = economyJson.getJSONArray("items")
                for (i in 0 until economyItemsArray.length()) {
                    val itemJson = economyItemsArray.getJSONObject(i)
                    economyItems.add(parseTaxiOption(itemJson))
                }
                
                withContext(Dispatchers.Main) {
                    taxiCategories = categories
                    recommendOptions = recommends
                    aggregateOptions = aggregates
                    economyGroup = TaxiGroup(
                        title = economyJson.getString("title"),
                        count = economyJson.getInt("count"),
                        isAllSelected = economyJson.getBoolean("isAllSelected"),
                        items = economyItems
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    Column(modifier = modifier) {
        Row(modifier = Modifier.weight(1f)) {
            // Left sidebar
            TaxiCategorySidebar(
                categories = taxiCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                modifier = Modifier.width(100.dp)
            )
            
            // Right content
            TaxiOptionsContent(
                recommendOptions = recommendOptions,
                aggregateOptions = aggregateOptions,
                economyGroup = economyGroup,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Bottom action bar
        TaxiBottomActionBar(
            selectedCount = selectedCount,
            estimatedPrice = generateTaxiPriceRange(),
            onTaxiClick = onTaxiClick
        )
    }
}

@Composable
fun TaxiCategorySidebar(
    categories: List<TaxiCategory>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(categories) { category ->
            TaxiCategoryItem(
                category = category,
                isSelected = category.id == selectedCategory,
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
fun TaxiCategoryItem(
    category: TaxiCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Text(
            text = category.name,
            fontSize = 14.sp,
            color = if (isSelected) Color(0xFF2196F3) else Color.Black,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.align(Alignment.Center)
        )
        
        category.badge?.let { badge ->
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp),
                shape = CircleShape,
                color = Color(0xFF2196F3)
            ) {
                Text(
                    text = badge.toString(),
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
fun TaxiOptionsContent(
    recommendOptions: List<TaxiOption>,
    aggregateOptions: List<TaxiOption>,
    economyGroup: TaxiGroup?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Recommendation options
        items(recommendOptions) { option ->
            TaxiRecommendationCard(option = option)
        }
        
        // Aggregate options
        items(aggregateOptions) { option ->
            TaxiAggregateCard(option = option)
        }
        
        // Economy group
        economyGroup?.let { group ->
            item {
                TaxiEconomyGroup(group = group)
            }
        }
        
        // Floating tip
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "上滑查看更多已选车型",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "上滑",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaxiBottomActionBar(
    selectedCount: Int,
    estimatedPrice: String,
    onTaxiClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .background(Color(0xFF1976D2))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "预估 $estimatedPrice",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "已选 ${selectedCount}个车型",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            Button(
                onClick = onTaxiClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "立即打车",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun generateRouteOptions(destination: String): List<RouteOption> {
    // Calculate distance and price based on destination
    val (distance, taxiPrice, busDuration) = when {
        destination.contains("巴奴") || destination.contains("火锅") -> Triple("1.8公里", "14元", "1小时22分钟")
        destination.contains("木屋") || destination.contains("烧烤") -> Triple("2.3公里", "18元", "1小时35分钟")
        destination.contains("麦当劳") -> Triple("0.8公里", "9元", "35分钟")
        destination.contains("老乡鸡") -> Triple("1.2公里", "12元", "45分钟")
        destination.contains("汉庭") || destination.contains("如家") -> Triple("3.1公里", "22元", "1小时45分钟")
        destination.contains("凯悦") -> Triple("4.2公里", "28元", "2小时10分钟")
        destination.contains("欢乐谷") -> Triple("8.5公里", "35元", "2小时30分钟")
        destination.contains("东湖") -> Triple("6.2公里", "30元", "2小时15分钟")
        destination.contains("黄鹤楼") -> Triple("5.8公里", "28元", "2小时5分钟")
        else -> Triple("2.5公里", "20元", "1小时30分钟")
    }
    
    return listOf(
        RouteOption(
            id = "route_001",
            transportationType = "公交",
            duration = busDuration,
            distance = distance,
            price = "2元",
            mainRoute = listOf(
                RouteSegment("步行", "12", "walk", null),
                RouteSegment("公交", "804路", "bus", "绿色"),
                RouteSegment("步行", "14", "walk", null)
            ),
            details = "15站 · 2元 · 雄楚大道BRT元宝山站上车",
            realTimeInfo = "804路 预计 20:23~20:33 到站",
            tags = emptyList(),
            isRecommended = true
        ),
        RouteOption(
            id = "route_002",
            transportationType = "混合",
            duration = "1小时1分钟",
            distance = distance,
            price = "约8元",
            mainRoute = listOf(
                RouteSegment("打车", "打车 约8元", "taxi", "蓝色"),
                RouteSegment("步行", "10", "walk", null),
                RouteSegment("地铁", "轨道交通 5号线", "subway", "蓝色"),
                RouteSegment("地铁", "轨道交通 11号线", "subway", "蓝色")
            ),
            details = "6站 · 12元 · 虎泉 (G2) 进站",
            realTimeInfo = "轨道交通 11号线 首班发车约 8分钟/趟",
            tags = listOf("限时特价", "打车混合"),
            isRecommended = false
        ),
        RouteOption(
            id = "route_003",
            transportationType = "轨道交通",
            duration = "58分钟",
            distance = distance,
            price = "4元",
            mainRoute = listOf(
                RouteSegment("步行", "31", "walk", null),
                RouteSegment("地铁", "换乘两次轨道交通", "subway", "蓝色")
            ),
            details = "6站 · 4元 · 虎泉 (G1口) 进站",
            realTimeInfo = "换乘等待约 8分钟",
            tags = emptyList(),
            isRecommended = false
        ),
        RouteOption(
            id = "route_004",
            transportationType = "打车",
            duration = "33分钟",
            distance = distance,
            price = taxiPrice,
            mainRoute = listOf(
                RouteSegment("打车", "一口价 $taxiPrice", "taxi", "蓝色")
            ),
            details = "$distance · 更快更省心 · 上门接送",
            realTimeInfo = "附近有车，预计 3分钟上车",
            tags = listOf("少步行"),
            isRecommended = false
        )
    )
}

private fun generateTaxiPriceRange(): String {
    return "17.1-22元起"
}

private fun parseTaxiOption(jsonObject: JSONObject): TaxiOption {
    val tagsArray = jsonObject.optJSONArray("tags")
    val tags = mutableListOf<String>()
    if (tagsArray != null) {
        for (i in 0 until tagsArray.length()) {
            tags.add(tagsArray.getString(i))
        }
    }
    
    return TaxiOption(
        id = jsonObject.getString("id"),
        type = jsonObject.getString("type"),
        name = jsonObject.getString("name"),
        subtitle = jsonObject.optString("subtitle").takeIf { it.isNotEmpty() },
        iconColor = jsonObject.getString("iconColor"),
        iconText = jsonObject.optString("iconText").takeIf { it.isNotEmpty() },
        price = jsonObject.getString("price"),
        priceRange = jsonObject.optString("priceRange").takeIf { it.isNotEmpty() },
        actionText = jsonObject.optString("actionText").takeIf { it.isNotEmpty() },
        discount = jsonObject.optString("discount").takeIf { it.isNotEmpty() },
        tags = tags,
        isSelected = jsonObject.getBoolean("isSelected"),
        logo = jsonObject.optString("logo").takeIf { it.isNotEmpty() }
    )
}

@Composable
fun TaxiRecommendationCard(option: TaxiOption) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = getIconBackgroundColor(option.iconColor)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = option.iconText ?: "🚗",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                option.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Price and action
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "一口价 ${option.price}",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
                
                option.actionText?.let { actionText ->
                    Surface(
                        modifier = Modifier.padding(top = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2196F3)
                    ) {
                        Text(
                            text = actionText,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaxiAggregateCard(option: TaxiOption) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = getIconBackgroundColor(option.iconColor)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "🚗",
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (option.name.contains("拼车")) "拼成价 " else "",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = option.price,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                option.priceRange?.let { priceRange ->
                    Text(
                        text = priceRange,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                option.discount?.let { discount ->
                    Surface(
                        modifier = Modifier.padding(top = 4.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFF5722)
                    ) {
                        Text(
                            text = discount,
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            // Checkbox
            Checkbox(
                checked = option.isSelected,
                onCheckedChange = { },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF2196F3)
                )
            )
        }
    }
}

@Composable  
fun TaxiEconomyGroup(group: TaxiGroup) {
    Column {
        // Group header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${group.title} (${group.count})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "全选${group.title}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Checkbox(
                    checked = group.isAllSelected,
                    onCheckedChange = { },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = Color.Gray
                    )
                )
            }
        }
        
        // Group items
        group.items.forEach { item ->
            TaxiProviderCard(option = item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TaxiProviderCard(option: TaxiOption) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Provider logo/icon
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = getIconBackgroundColor(option.iconColor)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = option.name.take(1),
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Provider info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                option.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Price section
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "预估",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = option.price,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                option.discount?.let { discount ->
                    Surface(
                        modifier = Modifier.padding(top = 2.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFF5722)
                    ) {
                        Text(
                            text = discount,
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Checkbox
            Checkbox(
                checked = option.isSelected,
                onCheckedChange = { },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF2196F3)
                )
            )
        }
    }
}

private fun getIconBackgroundColor(colorName: String): Color {
    return when (colorName) {
        "green" -> Color(0xFF4CAF50)
        "yellow" -> Color(0xFFFFC107)
        "orange" -> Color(0xFFFF9800)
        "blue" -> Color(0xFF2196F3)
        "dark" -> Color(0xFF424242)
        else -> Color(0xFF9E9E9E)
    }
}