package com.example.GaoDe.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TextButton
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

data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

data class TaxiData(
    val categories: List<TaxiCategory>,
    val recommendOptions: List<TaxiOption>,
    val aggregateOptions: List<TaxiOption>,
    val economyGroup: TaxiGroup
)

data class LocationPricing(
    val distance: String,
    val discountPrice: Int,  // 特惠快车价格
    val economyPrice: Int,   // 经济车价格  
    val premiumPrice: Int,   // 专车价格
    val busDuration: String,
    val taxiDuration: String
)

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
    var showWaypointSelector by remember { mutableStateOf(false) }
    var waypoint by remember { mutableStateOf<String?>(null) }
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
        // Show waypoint selector or normal route planning
        if (showWaypointSelector) {
            WaypointSelectorScreen(
                startLocation = startLocation,
                endLocation = endLocation,
                currentWaypoint = waypoint,
                onBackClick = { showWaypointSelector = false },
                onWaypointSelected = { selectedWaypoint ->
                    waypoint = selectedWaypoint
                    showWaypointSelector = false
                }
            )
        } else {
            // Top Bar
            PlanRouteTopBar(
                startLocation = startLocation,
                endLocation = endLocation,
                waypoint = waypoint,
                onBackClick = onBackClick,
                onWaypointClick = { showWaypointSelector = true }
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
                    destination = endLocation,
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
}

@Composable
fun PlanRouteTopBar(
    startLocation: String,
    endLocation: String,
    waypoint: String? = null,
    onBackClick: () -> Unit,
    onWaypointClick: () -> Unit = {}
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
            
            // Start and end locations with waypoint button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                    
                    // Waypoint if exists
                    waypoint?.let { waypointText ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF2196F3), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = waypointText,
                                fontSize = 16.sp,
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
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
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Waypoint button
                OutlinedButton(
                    onClick = onWaypointClick,
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2196F3)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF2196F3)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "途经点",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
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
    destination: String = "",
    onTaxiClick: () -> Unit = {}
) {
    var taxiCategories by remember { mutableStateOf<List<TaxiCategory>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("recommend") }
    var recommendOptions by remember { mutableStateOf<List<TaxiOption>>(emptyList()) }
    var aggregateOptions by remember { mutableStateOf<List<TaxiOption>>(emptyList()) }
    var economyGroup by remember { mutableStateOf<TaxiGroup?>(null) }
    var selectedTaxiIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedCount by remember { mutableIntStateOf(0) }
    var estimatedPrice by remember { mutableStateOf("17.1-22元起") }
    var currentLocationInfo by remember { mutableStateOf<LocationPricing?>(null) }
    
    val context = LocalContext.current
    
    LaunchedEffect(destination) {
        withContext(Dispatchers.IO) {
            try {
                // Generate dynamic taxi data based on destination
                val dynamicTaxiData = generateTaxiData(destination)
                val locationInfo = getLocationPricing(destination)
                
                withContext(Dispatchers.Main) {
                    taxiCategories = dynamicTaxiData.categories
                    recommendOptions = dynamicTaxiData.recommendOptions
                    aggregateOptions = dynamicTaxiData.aggregateOptions
                    economyGroup = dynamicTaxiData.economyGroup
                    currentLocationInfo = locationInfo
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    // Update content when category changes
    LaunchedEffect(selectedCategory, currentLocationInfo) {
        currentLocationInfo?.let { locationInfo ->
            when (selectedCategory) {
                "discountFast" -> {
                    estimatedPrice = "${locationInfo.discountPrice}-${locationInfo.discountPrice + 3}元起"
                    economyGroup = generateDiscountFastGroup(locationInfo.discountPrice)
                }
                "economy" -> {
                    estimatedPrice = "${locationInfo.economyPrice}-${locationInfo.economyPrice + 4}元起"
                    economyGroup = generateEconomyGroup(locationInfo.economyPrice)
                }
                "premium" -> {
                    estimatedPrice = "${locationInfo.economyPrice + 8}-${locationInfo.economyPrice + 15}元起"
                    economyGroup = generatePremiumComfortGroup(locationInfo.economyPrice + 8)
                }
                "specialCar" -> {
                    estimatedPrice = "${locationInfo.premiumPrice}-${locationInfo.premiumPrice + 6}元起"
                    economyGroup = generatePremiumGroup(locationInfo.premiumPrice)
                }
                else -> {
                    estimatedPrice = "${locationInfo.economyPrice}-${locationInfo.premiumPrice}元起"
                }
            }
        }
    }
    
    // Update selected count when selection changes
    LaunchedEffect(selectedTaxiIds) {
        selectedCount = selectedTaxiIds.size
    }
    
    // Helper function to toggle taxi selection
    val toggleTaxiSelection = { taxiId: String ->
        selectedTaxiIds = if (selectedTaxiIds.contains(taxiId)) {
            selectedTaxiIds - taxiId
        } else {
            selectedTaxiIds + taxiId
        }
    }
    
    // Helper function to toggle group selection
    val toggleGroupSelection = { groupIds: List<String> ->
        val allSelected = groupIds.all { selectedTaxiIds.contains(it) }
        selectedTaxiIds = if (allSelected) {
            // Remove all group items from selection
            selectedTaxiIds - groupIds.toSet()
        } else {
            // Add all group items to selection
            selectedTaxiIds + groupIds.toSet()
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
                selectedTaxiIds = selectedTaxiIds,
                onTaxiToggle = toggleTaxiSelection,
                onGroupToggle = toggleGroupSelection,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Bottom action bar
        TaxiBottomActionBar(
            selectedCount = selectedCount,
            estimatedPrice = estimatedPrice,
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
    selectedTaxiIds: Set<String>,
    onTaxiToggle: (String) -> Unit,
    onGroupToggle: (List<String>) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Recommendation options
        items(recommendOptions) { option ->
            TaxiRecommendationCard(
                option = option,
                isSelected = selectedTaxiIds.contains(option.id),
                onToggle = { onTaxiToggle(option.id) }
            )
        }
        
        // Aggregate options
        items(aggregateOptions) { option ->
            TaxiAggregateCard(
                option = option,
                isSelected = selectedTaxiIds.contains(option.id),
                onToggle = { onTaxiToggle(option.id) }
            )
        }
        
        // Economy group
        economyGroup?.let { group ->
            item {
                TaxiEconomyGroup(
                    group = group,
                    selectedTaxiIds = selectedTaxiIds,
                    onTaxiToggle = onTaxiToggle,
                    onGroupToggle = onGroupToggle
                )
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
                    text = "已选车型",
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
    val (distance, taxiPrice, busDuration, taxiDuration) = when {
        destination.contains("巴奴") || destination.contains("火锅") -> Quadruple("4.2公里", "25元", "1小时5分钟", "17分钟")
        destination.contains("木屋") || destination.contains("烧烤") -> Quadruple("890米", "起步价8元", "25分钟", "5分钟")
        destination.contains("麦当劳") -> Quadruple("3.5公里", "12元", "35分钟", "8分钟")
        destination.contains("老乡鸡") -> Quadruple("2.1公里", "16元", "45分钟", "12分钟")
        destination.contains("金雁") -> Quadruple("6.8公里", "35元", "1小时20分钟", "15分钟")
        destination.contains("汉庭") -> Quadruple("2.8公里", "22元", "1小时15分钟", "15分钟")
        destination.contains("如家") -> Quadruple("7.2公里", "22元", "1小时15分钟", "15分钟")
        destination.contains("凯悦") -> Quadruple("5.1公里", "35元", "1小时45分钟", "22分钟")
        destination.contains("欢乐谷") -> Quadruple("13.1公里", "48元", "2小时10分钟", "25分钟")
        destination.contains("东湖") -> Quadruple("8.5公里", "40元", "1小时55分钟", "28分钟")
        destination.contains("黄鹤楼") -> Quadruple("6.2公里", "32元", "1小时25分钟", "20分钟")
        destination.contains("南湖") -> Quadruple("5.8公里", "30元", "1小时", "12分钟")
        destination.contains("海昌") -> Quadruple("18.6公里", "55元", "2小时30分钟", "35分钟")
        else -> Quadruple("3.5公里", "20元", "1小时10分钟", "15分钟")
    }
    
    // Generate different route combinations based on destination
    return when {
        // 近距离地点：主要公交直达或少量步行
        destination.contains("木屋") || destination.contains("麦当劳") -> generateNearbyRoutes(distance, taxiPrice, taxiDuration)
        // 中距离地点：多种交通方式组合
        destination.contains("老乡鸡") || destination.contains("汉庭") || destination.contains("如家") -> generateMidDistanceRoutes(distance, taxiPrice, busDuration, taxiDuration)
        // 远距离地点：需要换乘的复杂路线
        destination.contains("凯悦") || destination.contains("欢乐谷") || destination.contains("东湖") || destination.contains("黄鹤楼") -> generateLongDistanceRoutes(distance, taxiPrice, busDuration, taxiDuration)
        // 火锅店等特定地点：复合交通方式
        destination.contains("巴奴") || destination.contains("火锅") -> generateComplexRoutes(distance, taxiPrice, busDuration, taxiDuration)
        // 默认路线
        else -> generateDefaultRoutes(distance, taxiPrice, busDuration, taxiDuration)
    }
}

private fun generateNearbyRoutes(distance: String, taxiPrice: String, taxiDuration: String): List<RouteOption> {
    return listOf(
        RouteOption(
            id = "route_001",
            transportationType = "公交",
            duration = "18分钟",
            distance = distance,
            price = "2元",
            mainRoute = listOf(
                RouteSegment("步行", "3分钟", "walk", null),
                RouteSegment("公交", "586路", "bus", "绿色")
            ),
            details = "3站 · 2元 · 广八路公交站上车",
            realTimeInfo = "586路 预计 2分钟到站",
            tags = listOf("少步行"),
            isRecommended = true
        ),
        RouteOption(
            id = "route_002",
            transportationType = "步行",
            duration = "12分钟",
            distance = distance,
            price = null,
            mainRoute = listOf(
                RouteSegment("步行", "12分钟", "walk", null)
            ),
            details = "全程步行 · 无红绿灯",
            realTimeInfo = null,
            tags = listOf("健康出行"),
            isRecommended = false
        ),
        RouteOption(
            id = "route_003",
            transportationType = "打车",
            duration = taxiDuration,
            distance = distance,
            price = taxiPrice,
            mainRoute = listOf(
                RouteSegment("打车", "一口价 $taxiPrice", "taxi", "蓝色")
            ),
            details = "$distance · 上门接送",
            realTimeInfo = "附近有车，预计 1分钟上车",
            tags = listOf("最快"),
            isRecommended = false
        )
    )
}

private fun generateMidDistanceRoutes(distance: String, taxiPrice: String, busDuration: String, taxiDuration: String): List<RouteOption> {
    return listOf(
        RouteOption(
            id = "route_001",
            transportationType = "公交",
            duration = "42分钟",
            distance = distance,
            price = "2元",
            mainRoute = listOf(
                RouteSegment("步行", "8分钟", "walk", null),
                RouteSegment("公交", "702路", "bus", "绿色"),
                RouteSegment("步行", "5分钟", "walk", null)
            ),
            details = "8站 · 2元 · 雄楚大道公交站上车",
            realTimeInfo = "702路 预计 5分钟到站",
            tags = emptyList(),
            isRecommended = true
        ),
        RouteOption(
            id = "route_002",
            transportationType = "地铁",
            duration = "35分钟",
            distance = distance,
            price = "3元",
            mainRoute = listOf(
                RouteSegment("步行", "12分钟", "walk", null),
                RouteSegment("地铁", "2号线", "subway", "蓝色"),
                RouteSegment("步行", "8分钟", "walk", null)
            ),
            details = "4站 · 3元 · 虎泉站 (B口) 进站",
            realTimeInfo = "2号线 首班发车约 4分钟",
            tags = listOf("地铁直达"),
            isRecommended = false
        ),
        RouteOption(
            id = "route_003",
            transportationType = "混合",
            duration = "38分钟",
            distance = distance,
            price = "约10元",
            mainRoute = listOf(
                RouteSegment("打车", "打车 6元", "taxi", "蓝色"),
                RouteSegment("地铁", "2号线", "subway", "蓝色"),
                RouteSegment("步行", "5分钟", "walk", null)
            ),
            details = "3站 · 打车+地铁 · 螃蟹岬站进站",
            realTimeInfo = "2号线 首班发车约 3分钟",
            tags = listOf("少步行", "混合模式"),
            isRecommended = false
        ),
        RouteOption(
            id = "route_004",
            transportationType = "打车",
            duration = taxiDuration,
            distance = distance,
            price = taxiPrice,
            mainRoute = listOf(
                RouteSegment("打车", "一口价 $taxiPrice", "taxi", "蓝色")
            ),
            details = "$distance · 更快更省心 · 上门接送",
            realTimeInfo = "附近有车，预计 3分钟上车",
            tags = listOf("最快"),
            isRecommended = false
        )
    )
}

private fun generateLongDistanceRoutes(distance: String, taxiPrice: String, busDuration: String, taxiDuration: String): List<RouteOption> {
    return listOf(
        RouteOption(
            id = "route_001",
            transportationType = "地铁",
            duration = "1小时15分钟",
            distance = distance,
            price = "5元",
            mainRoute = listOf(
                RouteSegment("步行", "15分钟", "walk", null),
                RouteSegment("地铁", "2号线", "subway", "蓝色"),
                RouteSegment("地铁", "4号线", "subway", "绿色"),
                RouteSegment("步行", "12分钟", "walk", null)
            ),
            details = "地铁换乘 · 中南路站换乘 · 5元",
            realTimeInfo = "换乘等待约 5分钟",
            tags = listOf("换乘1次"),
            isRecommended = true
        ),
        RouteOption(
            id = "route_002",
            transportationType = "公交",
            duration = busDuration,
            distance = distance,
            price = "4元",
            mainRoute = listOf(
                RouteSegment("步行", "8分钟", "walk", null),
                RouteSegment("公交", "538路", "bus", "绿色"),
                RouteSegment("公交", "413路", "bus", "蓝色"),
                RouteSegment("步行", "10分钟", "walk", null)
            ),
            details = "公交换乘 · 中南路站换乘 · 4元",
            realTimeInfo = "538路 预计 12分钟到站",
            tags = listOf("换乘1次"),
            isRecommended = false
        ),
        RouteOption(
            id = "route_003",
            transportationType = "混合",
            duration = "1小时8分钟",
            distance = distance,
            price = "约15元",
            mainRoute = listOf(
                RouteSegment("打车", "打车 10元", "taxi", "蓝色"),
                RouteSegment("地铁", "4号线", "subway", "绿色"),
                RouteSegment("步行", "8分钟", "walk", null)
            ),
            details = "打车+地铁 · 王家湾站进站 · 约15元",
            realTimeInfo = "4号线 首班发车约 6分钟",
            tags = listOf("少步行", "混合模式"),
            isRecommended = false
        ),
        RouteOption(
            id = "route_004",
            transportationType = "打车",
            duration = taxiDuration,
            distance = distance,
            price = taxiPrice,
            mainRoute = listOf(
                RouteSegment("打车", "一口价 $taxiPrice", "taxi", "蓝色")
            ),
            details = "$distance · 高速直达 · 上门接送",
            realTimeInfo = "附近有车，预计 5分钟上车",
            tags = listOf("最快", "直达"),
            isRecommended = false
        )
    )
}

private fun generateComplexRoutes(distance: String, taxiPrice: String, busDuration: String, taxiDuration: String): List<RouteOption> {
    return listOf(
        RouteOption(
            id = "route_001",
            transportationType = "公交",
            duration = busDuration,
            distance = distance,
            price = "2元",
            mainRoute = listOf(
                RouteSegment("步行", "12分钟", "walk", null),
                RouteSegment("公交", "804路", "bus", "绿色"),
                RouteSegment("步行", "8分钟", "walk", null)
            ),
            details = "15站 · 2元 · 雄楚大道BRT元宝山站上车",
            realTimeInfo = "804路 预计 8分钟到站",
            tags = listOf("BRT快速公交"),
            isRecommended = true
        ),
        RouteOption(
            id = "route_002",
            transportationType = "混合",
            duration = "58分钟",
            distance = distance,
            price = "约12元",
            mainRoute = listOf(
                RouteSegment("打车", "打车 8元", "taxi", "蓝色"),
                RouteSegment("步行", "3分钟", "walk", null),
                RouteSegment("地铁", "5号线", "subway", "蓝色"),
                RouteSegment("地铁", "11号线", "subway", "紫色")
            ),
            details = "地铁换乘 · 宝通寺站换乘 · 12元",
            realTimeInfo = "地铁首班发车约 5分钟",
            tags = listOf("限时特价", "混合模式"),
            isRecommended = false
        ),
        RouteOption(
            id = "route_003",
            transportationType = "地铁",
            duration = "52分钟",
            distance = distance,
            price = "4元",
            mainRoute = listOf(
                RouteSegment("步行", "18分钟", "walk", null),
                RouteSegment("地铁", "5号线", "subway", "蓝色"),
                RouteSegment("地铁", "11号线", "subway", "紫色"),
                RouteSegment("步行", "6分钟", "walk", null)
            ),
            details = "地铁换乘 · 宝通寺站换乘 · 4元",
            realTimeInfo = "换乘等待约 3分钟",
            tags = listOf("换乘1次"),
            isRecommended = false
        ),
        RouteOption(
            id = "route_004",
            transportationType = "打车",
            duration = taxiDuration,
            distance = distance,
            price = taxiPrice,
            mainRoute = listOf(
                RouteSegment("打车", "一口价 $taxiPrice", "taxi", "蓝色")
            ),
            details = "$distance · 避开拥堵路段 · 上门接送",
            realTimeInfo = "附近有车，预计 3分钟上车",
            tags = listOf("最快", "智能路线"),
            isRecommended = false
        )
    )
}

private fun generateDefaultRoutes(distance: String, taxiPrice: String, busDuration: String, taxiDuration: String): List<RouteOption> {
    return listOf(
        RouteOption(
            id = "route_001",
            transportationType = "公交",
            duration = "45分钟",
            distance = distance,
            price = "2元",
            mainRoute = listOf(
                RouteSegment("步行", "10分钟", "walk", null),
                RouteSegment("公交", "518路", "bus", "绿色"),
                RouteSegment("步行", "6分钟", "walk", null)
            ),
            details = "9站 · 2元 · 珞喻路公交站上车",
            realTimeInfo = "518路 预计 8分钟到站",
            tags = emptyList(),
            isRecommended = true
        ),
        RouteOption(
            id = "route_002",
            transportationType = "地铁",
            duration = "38分钟",
            distance = distance,
            price = "3元",
            mainRoute = listOf(
                RouteSegment("步行", "15分钟", "walk", null),
                RouteSegment("地铁", "2号线", "subway", "蓝色"),
                RouteSegment("步行", "8分钟", "walk", null)
            ),
            details = "5站 · 3元 · 虎泉站进站",
            realTimeInfo = "2号线 首班发车约 6分钟",
            tags = listOf("地铁直达"),
            isRecommended = false
        ),
        RouteOption(
            id = "route_003",
            transportationType = "打车",
            duration = taxiDuration,
            distance = distance,
            price = taxiPrice,
            mainRoute = listOf(
                RouteSegment("打车", "一口价 $taxiPrice", "taxi", "蓝色")
            ),
            details = "$distance · 更快更省心 · 上门接送",
            realTimeInfo = "附近有车，预计 4分钟上车",
            tags = listOf("便捷"),
            isRecommended = false
        )
    )
}

private fun generateTaxiData(destination: String): TaxiData {
    // Get distance and pricing info for destination with more detailed breakdown
    val locationInfo = when {
        destination.contains("巴奴") || destination.contains("火锅") -> 
            LocationPricing("4.2公里", 19, 25, 38, "1小时5分钟", "17分钟")
        destination.contains("木屋") || destination.contains("烧烤") -> 
            LocationPricing("890米", 6, 8, 12, "25分钟", "5分钟")
        destination.contains("麦当劳") -> 
            LocationPricing("3.5公里", 9, 12, 18, "35分钟", "8分钟")
        destination.contains("老乡鸡") -> 
            LocationPricing("2.1公里", 12, 16, 24, "45分钟", "12分钟")
        destination.contains("金雁") -> 
            LocationPricing("6.8公里", 26, 35, 53, "1小时20分钟", "15分钟")
        destination.contains("汉庭") -> 
            LocationPricing("2.8公里", 16, 22, 33, "1小时15分钟", "15分钟")
        destination.contains("如家") -> 
            LocationPricing("7.2公里", 16, 22, 33, "1小时15分钟", "15分钟")
        destination.contains("凯悦") -> 
            LocationPricing("5.1公里", 26, 35, 53, "1小时45分钟", "22分钟")
        destination.contains("欢乐谷") -> 
            LocationPricing("13.1公里", 36, 48, 72, "2小时10分钟", "25分钟")
        destination.contains("东湖") -> 
            LocationPricing("8.5公里", 30, 40, 60, "1小时55分钟", "28分钟")
        destination.contains("黄鹤楼") -> 
            LocationPricing("6.2公里", 24, 32, 48, "1小时25分钟", "20分钟")
        destination.contains("南湖") -> 
            LocationPricing("5.8公里", 22, 30, 45, "1小时", "12分钟")
        destination.contains("海昌") -> 
            LocationPricing("18.6公里", 42, 55, 82, "2小时30分钟", "35分钟")
        else -> LocationPricing("3.5公里", 15, 20, 30, "1小时10分钟", "15分钟")
    }

    val categories = listOf(
        TaxiCategory("recommend", "推荐", null, true),
        TaxiCategory("carpool", "拼车", null, false),
        TaxiCategory("discountFast", "特惠快车", 17, false),
        TaxiCategory("economy", "经济", 14, false),
        TaxiCategory("fastCar", "特快车", null, false),
        TaxiCategory("taxi", "出租", null, false),
        TaxiCategory("premium", "优享", 1, false),
        TaxiCategory("specialCar", "专车", null, false),
        TaxiCategory("sixSeater", "六座商务", null, false),
        TaxiCategory("luxury", "豪华", null, false)
    )

    val recommendOptions = listOf(
        TaxiOption(
            id = "recommend_001",
            type = "recommendation",
            name = "顺风车",
            subtitle = "信赖获评高路线",
            iconColor = "green",
            iconText = "顺",
            price = "${locationInfo.discountPrice - 3}元",
            priceRange = null,
            actionText = "去体验",
            discount = null,
            tags = emptyList(),
            isSelected = false,
            logo = null
        )
    )

    val aggregateOptions = listOf(
        TaxiOption(
            id = "aggregate_001",
            type = "aggregate",
            name = "极速拼车",
            subtitle = "拼成优享",
            iconColor = "blue",
            iconText = null,
            price = "${locationInfo.economyPrice + 5}.7元",
            priceRange = "${locationInfo.economyPrice + 8}-${locationInfo.economyPrice + 12}元",
            actionText = null,
            discount = null,
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "aggregate_002",
            type = "aggregate",
            name = "特惠快车",
            subtitle = null,
            iconColor = "orange",
            iconText = null,
            price = "${locationInfo.discountPrice}.1-${locationInfo.discountPrice + 5}.2元",
            priceRange = null,
            actionText = null,
            discount = "限时特惠代金券",
            tags = emptyList(),
            isSelected = false,
            logo = null
        )
    )

    val economyItems = listOf(
        TaxiOption(
            id = "eco_001",
            type = "provider",
            name = "T3出行",
            subtitle = "敬业数码 隐私保护",
            iconColor = "orange",
            iconText = null,
            price = "${locationInfo.economyPrice}元",
            priceRange = null,
            actionText = null,
            discount = "限量 已优惠2元",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "eco_002",
            type = "provider",
            name = "旅程约车",
            subtitle = "敬业数码 隐私保护",
            iconColor = "blue",
            iconText = null,
            price = "${locationInfo.economyPrice}元",
            priceRange = null,
            actionText = null,
            discount = "限量 已优惠2元",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "eco_003",
            type = "provider",
            name = "黄鹤行",
            subtitle = "敬业数码 隐私保护",
            iconColor = "green",
            iconText = null,
            price = "${locationInfo.economyPrice}元",
            priceRange = null,
            actionText = null,
            discount = "限量 已优惠1元",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "eco_004",
            type = "provider",
            name = "AA出行",
            subtitle = "敬业数码 隐私保护",
            iconColor = "purple",
            iconText = null,
            price = "${locationInfo.economyPrice}元",
            priceRange = null,
            actionText = null,
            discount = "限量 已优惠1元",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "eco_005",
            type = "provider",
            name = "星徽出行",
            subtitle = "敬业数码 隐私保护",
            iconColor = "brown",
            iconText = null,
            price = "${locationInfo.economyPrice + 1}元",
            priceRange = null,
            actionText = null,
            discount = "限量 已优惠1元",
            tags = emptyList(),
            isSelected = false,
            logo = null
        )
    )

    val economyGroup = TaxiGroup(
        title = "经济型 (14)",
        count = 14,
        isAllSelected = false,
        items = economyItems
    )

    return TaxiData(categories, recommendOptions, aggregateOptions, economyGroup)
}

private fun getLocationPricing(destination: String): LocationPricing {
    return when {
        destination.contains("巴奴") || destination.contains("火锅") -> 
            LocationPricing("4.2公里", 19, 25, 38, "1小时5分钟", "17分钟")
        destination.contains("木屋") || destination.contains("烧烤") -> 
            LocationPricing("890米", 6, 8, 12, "25分钟", "5分钟")
        destination.contains("麦当劳") -> 
            LocationPricing("3.5公里", 9, 12, 18, "35分钟", "8分钟")
        destination.contains("老乡鸡") -> 
            LocationPricing("2.1公里", 12, 16, 24, "45分钟", "12分钟")
        destination.contains("金雁") -> 
            LocationPricing("6.8公里", 26, 35, 53, "1小时20分钟", "15分钟")
        destination.contains("汉庭") -> 
            LocationPricing("2.8公里", 16, 22, 33, "1小时15分钟", "15分钟")
        destination.contains("如家") -> 
            LocationPricing("7.2公里", 16, 22, 33, "1小时15分钟", "15分钟")
        destination.contains("凯悦") -> 
            LocationPricing("5.1公里", 26, 35, 53, "1小时45分钟", "22分钟")
        destination.contains("欢乐谷") -> 
            LocationPricing("13.1公里", 36, 48, 72, "2小时10分钟", "25分钟")
        destination.contains("东湖") -> 
            LocationPricing("8.5公里", 30, 40, 60, "1小时55分钟", "28分钟")
        destination.contains("黄鹤楼") -> 
            LocationPricing("6.2公里", 24, 32, 48, "1小时25分钟", "20分钟")
        else -> LocationPricing("3.5公里", 15, 20, 30, "1小时10分钟", "15分钟")
    }
}

private fun generateDiscountFastGroup(basePrice: Int): TaxiGroup {
    val items = listOf(
        TaxiOption(
            id = "discount_001",
            type = "provider",
            name = "快车普通",
            subtitle = "舒适出行 价格优惠",
            iconColor = "orange",
            iconText = null,
            price = "${basePrice}元",
            priceRange = null,
            actionText = null,
            discount = "特惠减2元",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "discount_002",
            type = "provider",
            name = "东风出行",
            subtitle = "本地品牌 服务贴心",
            iconColor = "blue",
            iconText = null,
            price = "${basePrice + 1}元",
            priceRange = null,
            actionText = null,
            discount = "新用户减3元",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "discount_003",
            type = "provider",
            name = "飞嘀打车",
            subtitle = "快速响应 准时到达",
            iconColor = "green",
            iconText = null,
            price = "${basePrice + 2}元",
            priceRange = null,
            actionText = null,
            discount = "限时特惠",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "discount_004", 
            type = "provider",
            name = "曹操出行",
            subtitle = "新能源 绿色出行",
            iconColor = "green",
            iconText = null,
            price = "${basePrice + 1}元",
            priceRange = null,
            actionText = null,
            discount = "环保优惠1元",
            tags = emptyList(),
            isSelected = false,
            logo = null
        )
    )
    
    return TaxiGroup(
        title = "特惠快车",
        count = 8,
        isAllSelected = false,
        items = items
    )
}

private fun generateEconomyGroup(basePrice: Int): TaxiGroup {
    val items = listOf(
        TaxiOption(
            id = "eco_001",
            type = "provider",
            name = "T3出行",
            subtitle = "合规运营 安全可靠",
            iconColor = "orange",
            iconText = null,
            price = "${basePrice}元",
            priceRange = null,
            actionText = null,
            discount = "优惠券减2元",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "eco_002",
            type = "provider",
            name = "神州专车",
            subtitle = "专业司机 品质服务",
            iconColor = "blue",
            iconText = null,
            price = "${basePrice + 2}元",
            priceRange = null,
            actionText = null,
            discount = "品质保障",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "eco_003",
            type = "provider",
            name = "首汽约车",
            subtitle = "国企背景 服务标准",
            iconColor = "blue",
            iconText = null,
            price = "${basePrice + 1}元",
            priceRange = null,
            actionText = null,
            discount = "会员优惠",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "eco_004",
            type = "provider",
            name = "嘀嗒出行",
            subtitle = "顺风车服务 环保出行",
            iconColor = "green",
            iconText = null,
            price = "${basePrice}元",
            priceRange = null,
            actionText = null,
            discount = "拼车减3元",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "eco_005",
            type = "provider",
            name = "万顺叫车",
            subtitle = "全国连锁 统一标准",
            iconColor = "purple",
            iconText = null,
            price = "${basePrice + 3}元",
            priceRange = null,
            actionText = null,
            discount = "新手优惠",
            tags = emptyList(),
            isSelected = false,
            logo = null
        )
    )

    return TaxiGroup(
        title = "经济型",
        count = 14,
        isAllSelected = false,
        items = items
    )
}

private fun generatePremiumComfortGroup(basePrice: Int): TaxiGroup {
    val items = listOf(
        TaxiOption(
            id = "comfort_001",
            type = "provider",
            name = "舒适专车",
            subtitle = "中级车型 舒适体验",
            iconColor = "blue",
            iconText = null,
            price = "${basePrice}元",
            priceRange = null,
            actionText = null,
            discount = "优享级服务",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "comfort_002",
            type = "provider",
            name = "滴滴优享",
            subtitle = "中高级车型 品质保证",
            iconColor = "orange",
            iconText = null,
            price = "${basePrice + 2}元",
            priceRange = null,
            actionText = null,
            discount = "品质优选",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "comfort_003",
            type = "provider",
            name = "首汽优享",
            subtitle = "B级车型 专业司机",
            iconColor = "blue",
            iconText = null,
            price = "${basePrice + 1}元",
            priceRange = null,
            actionText = null,
            discount = "国企服务",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "comfort_004",
            type = "provider",
            name = "曹操优享",
            subtitle = "新能源B级车 绿色出行",
            iconColor = "green",
            iconText = null,
            price = "${basePrice + 3}元",
            priceRange = null,
            actionText = null,
            discount = "绿色优享",
            tags = emptyList(),
            isSelected = false,
            logo = null
        )
    )

    return TaxiGroup(
        title = "优享",
        count = 6,
        isAllSelected = false,
        items = items
    )
}

private fun generatePremiumGroup(basePrice: Int): TaxiGroup {
    val items = listOf(
        TaxiOption(
            id = "premium_001",
            type = "provider",
            name = "礼橙专车",
            subtitle = "高端车型 尊享服务",
            iconColor = "orange",
            iconText = null,
            price = "${basePrice}元",
            priceRange = null,
            actionText = null,
            discount = "豪华升级",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "premium_002",
            type = "provider",
            name = "滴滴专车",
            subtitle = "BBA车型 专业司机",
            iconColor = "black",
            iconText = null,
            price = "${basePrice + 5}元",
            priceRange = null,
            actionText = null,
            discount = "高端享受",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "premium_003",
            type = "provider",
            name = "AA专车",
            subtitle = "商务车型 贵宾级服务",
            iconColor = "blue",
            iconText = null,
            price = "${basePrice + 3}元",
            priceRange = null,
            actionText = null,
            discount = "商务优选",
            tags = emptyList(),
            isSelected = false,
            logo = null
        ),
        TaxiOption(
            id = "premium_004",
            type = "provider",
            name = "易到用车",
            subtitle = "奢华体验 专属服务",
            iconColor = "gold",
            iconText = null,
            price = "${basePrice + 8}元",
            priceRange = null,
            actionText = null,
            discount = "VIP专享",
            tags = emptyList(),
            isSelected = false,
            logo = null
        )
    )

    return TaxiGroup(
        title = "专车",
        count = 6,
        isAllSelected = false,
        items = items
    )
}

private fun generateTaxiPriceRange(): String {
    return "17.1-22元起"
}

private fun generateTaxiPriceRange(multiplier: Float): String {
    val baseMin = 17.1f
    val baseMax = 22.0f
    val adjustedMin = (baseMin * multiplier).toInt()
    val adjustedMax = (baseMax * multiplier).toInt()
    return "${adjustedMin}-${adjustedMax}元起"
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
fun TaxiRecommendationCard(
    option: TaxiOption,
    isSelected: Boolean = false,
    onToggle: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
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
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Checkbox
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF2196F3)
                )
            )
        }
    }
}

@Composable
fun TaxiAggregateCard(
    option: TaxiOption,
    isSelected: Boolean = false,
    onToggle: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
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
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF2196F3)
                )
            )
        }
    }
}

@Composable  
fun TaxiEconomyGroup(
    group: TaxiGroup,
    selectedTaxiIds: Set<String>,
    onTaxiToggle: (String) -> Unit,
    onGroupToggle: (List<String>) -> Unit = {}
) {
    // Calculate if all items in the group are selected
    val groupItemIds = group.items.map { it.id }
    val isAllSelected = groupItemIds.all { selectedTaxiIds.contains(it) }
    
    val toggleAllSelection = {
        if (isAllSelected) {
            // If all selected, unselect all items in this group
            onGroupToggle(groupItemIds)
        } else {
            // If not all selected, select all items in this group
            onGroupToggle(groupItemIds)
        }
    }
    
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
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { toggleAllSelection() }
            ) {
                Text(
                    text = "全选${group.title}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Checkbox(
                    checked = isAllSelected,
                    onCheckedChange = { toggleAllSelection() },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = Color.Gray
                    )
                )
            }
        }
        
        // Group items
        group.items.forEach { item ->
            TaxiProviderCard(
                option = item,
                isSelected = selectedTaxiIds.contains(item.id),
                onToggle = { onTaxiToggle(item.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TaxiProviderCard(
    option: TaxiOption,
    isSelected: Boolean = false,
    onToggle: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
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
                checked = isSelected,
                onCheckedChange = { onToggle() },
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
        "black" -> Color(0xFF212121)
        "purple" -> Color(0xFF9C27B0)
        "gold" -> Color(0xFFFFD700)
        else -> Color(0xFF9E9E9E)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaypointSelectorScreen(
    startLocation: String,
    endLocation: String,
    currentWaypoint: String? = null,
    onBackClick: () -> Unit,
    onWaypointSelected: (String) -> Unit
) {
    var searchText by remember { mutableStateOf(currentWaypoint ?: "") }
    var showPlaceList by remember { mutableStateOf(false) }
    var availablePlaces by remember { mutableStateOf<List<PlaceInfo>>(emptyList()) }
    val context = LocalContext.current
    
    // Load all available places
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val places = loadAllPlaces(context)
                withContext(Dispatchers.Main) {
                    availablePlaces = places
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        WaypointTopBar(
            onBackClick = onBackClick,
            onConfirmClick = {
                if (searchText.isNotEmpty()) {
                    onWaypointSelected(searchText)
                }
            }
        )
        
        // Route Display
        WaypointRouteDisplay(
            startLocation = startLocation,
            waypoint = searchText.takeIf { it.isNotEmpty() },
            endLocation = endLocation,
            onWaypointClick = { showPlaceList = true }
        )
        
        if (showPlaceList) {
            // Place selection list with categories
            PlaceCategoryList(
                places = availablePlaces,
                onPlaceSelected = { place ->
                    searchText = place.name
                    showPlaceList = false
                }
            )
        } else {
            // Search suggestions or empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "点击“途经点”选择地点",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { showPlaceList = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2196F3)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2196F3))
                    ) {
                        Text(
                            text = "选择地点",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WaypointTopBar(
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
            
            Text(
                text = "添加途经点",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            TextButton(onClick = onConfirmClick) {
                Text(
                    text = "确定",
                    fontSize = 16.sp,
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
fun WaypointRouteDisplay(
    startLocation: String,
    waypoint: String?,
    endLocation: String,
    onWaypointClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF8F8F8)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Waypoint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onWaypointClick() }
                    .border(1.dp, Color(0xFF2196F3), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF2196F3), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = waypoint ?: "途经点",
                    fontSize = 16.sp,
                    color = if (waypoint != null) Color.Black else Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "选择",
                    tint = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                    color = Color.Black
                )
            }
        }
    }
}

data class PlaceInfo(
    val id: String,
    val name: String,
    val address: String,
    val category: String
)

@Composable
fun PlaceSelectionItem(
    place: PlaceInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = getCategoryColor(place.category)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = getCategoryIcon(place.category),
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Place info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = place.address,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "选择",
                tint = Color.Gray
            )
        }
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        "餐饮" -> Color(0xFFFF9800)
        "酒店" -> Color(0xFF2196F3)
        "景点" -> Color(0xFF4CAF50)
        else -> Color(0xFF9E9E9E)
    }
}

private fun getCategoryIcon(category: String): String {
    return when (category) {
        "餐饮" -> "🍽️"
        "酒店" -> "🏨"
        "景点" -> "🏞️"
        else -> "📍"
    }
}

@Composable
fun PlaceCategoryList(
    places: List<PlaceInfo>,
    onPlaceSelected: (PlaceInfo) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("美食") }
    val categories = listOf("美食", "酒店", "景点")
    
    val filteredPlaces = places.filter { place ->
        when (selectedCategory) {
            "美食" -> place.category == "餐饮"
            "酒店" -> place.category == "酒店"
            "景点" -> place.category == "景点"
            else -> false
        }
    }
    
    Column {
        // Category tabs
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFF8F8F8)
        ) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryTab(
                        category = category,
                        isSelected = category == selectedCategory,
                        onClick = { selectedCategory = category }
                    )
                }
            }
        }
        
        // Places list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredPlaces) { place ->
                PlaceSelectionItem(
                    place = place,
                    onClick = { onPlaceSelected(place) }
                )
            }
        }
    }
}

@Composable
fun CategoryTab(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = category,
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

private suspend fun loadAllPlaces(context: android.content.Context): List<PlaceInfo> {
    val places = mutableListOf<PlaceInfo>()
    
    try {
        // Load restaurants
        val restaurantsJson = context.assets.open("data/poi_restaurants.json").bufferedReader().use { it.readText() }
        val restaurantsArray = JSONArray(restaurantsJson)
        for (i in 0 until restaurantsArray.length()) {
            val restaurant = restaurantsArray.getJSONObject(i)
            places.add(
                PlaceInfo(
                    id = restaurant.getString("id"),
                    name = restaurant.getString("brandName"),
                    address = restaurant.getString("address"),
                    category = "餐饮"
                )
            )
        }
        
        // Load hotels
        val hotelsJson = context.assets.open("data/poi_hotels.json").bufferedReader().use { it.readText() }
        val hotelsArray = JSONArray(hotelsJson)
        for (i in 0 until hotelsArray.length()) {
            val hotel = hotelsArray.getJSONObject(i)
            places.add(
                PlaceInfo(
                    id = hotel.getString("id"),
                    name = hotel.getString("hotelName"),
                    address = hotel.getString("address"),
                    category = "酒店"
                )
            )
        }
        
        // Load scenic spots from places.json (filter by category and exclude specific places)
        val placesJson = context.assets.open("data/places.json").bufferedReader().use { it.readText() }
        val placesArray = JSONArray(placesJson)
        val excludedNames = setOf("上海外滩", "天安门广场")
        
        for (i in 0 until placesArray.length()) {
            val place = placesArray.getJSONObject(i)
            val category = place.getString("category")
            val name = place.getString("name")
            
            if (category == "景点" && !excludedNames.contains(name)) {
                places.add(
                    PlaceInfo(
                        id = place.getString("id"),
                        name = name,
                        address = place.getString("address"),
                        category = "景点"
                    )
                )
            }
        }
        
    } catch (e: Exception) {
        // Handle error
    }
    
    return places.sortedBy { it.name }
}