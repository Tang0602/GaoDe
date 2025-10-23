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
    onBackClick: () -> Unit = {}
) {
    var routeOptions by remember { mutableStateOf<List<RouteOption>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTransportMode by remember { mutableStateOf("公共交通") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("data/route_options.json")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)
                val items = mutableListOf<RouteOption>()
                
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    items.add(parseRouteOption(jsonObject))
                }
                
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
            // Route Options List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(routeOptions) { route ->
                    RouteOptionCard(
                        route = route,
                        onClick = { /* Handle route selection */ }
                    )
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