package com.example.GaoDe.ui.home

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.BitmapFactory
import com.example.GaoDe.model.ScenicSpotItem
import com.example.GaoDe.model.TicketInfo
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenicSpotResultsListScreen(
    searchCategory: String = "景点",
    onBackClick: () -> Unit = {},
    onScenicSpotClick: (String) -> Unit = {}
) {
    var scenicSpotList by remember { mutableStateOf<List<ScenicSpotItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("data/poi_scenic_spots.json")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)
                val items = mutableListOf<ScenicSpotItem>()
                
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    items.add(parseScenicSpotItem(jsonObject))
                }
                
                withContext(Dispatchers.Main) {
                    scenicSpotList = items
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
        // Scenic Spot Top Bar
        ScenicSpotTopBar(
            searchCategory = searchCategory,
            onBackClick = onBackClick
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Scenic Spot List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(scenicSpotList) { scenicSpot ->
                    ScenicSpotListItem(
                        scenicSpot = scenicSpot,
                        onClick = { onScenicSpotClick(scenicSpot.id) }
                    )
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray.copy(alpha = 0.1f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun ScenicSpotTopBar(
    searchCategory: String,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // City and category
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "武汉市",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "展开",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = searchCategory,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Headset,
                        contentDescription = "客服",
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
    }
}

@Composable
fun ScenicSpotListItem(
    scenicSpot: ScenicSpotItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var logoImageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    LaunchedEffect(scenicSpot.logo) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("avatar/${scenicSpot.logo}")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                withContext(Dispatchers.Main) {
                    logoImageBitmap = bitmap.asImageBitmap()
                }
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Scenic Spot Image
            Box(
                modifier = Modifier.size(80.dp)
            ) {
                if (logoImageBitmap != null) {
                    Image(
                        bitmap = logoImageBitmap!!,
                        contentDescription = scenicSpot.spotName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🏞️",
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Scenic Spot Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Spot name and level
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = scenicSpot.spotName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    SpotLevelTag(spotLevel = scenicSpot.spotLevel)
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Address and travel info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${scenicSpot.address.take(15)}...",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = scenicSpot.distance,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = scenicSpot.travelTime,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Rating and activity info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2196F3)
                    ) {
                        Text(
                            text = "${scenicSpot.rating} ${scenicSpot.ratingText}",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = scenicSpot.activityInfo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Ranking and service tags
                FlowRow(
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 4.dp
                ) {
                    scenicSpot.rankingInfo?.let { ranking ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFF5722)
                        ) {
                            Text(
                                text = ranking,
                                fontSize = 10.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    scenicSpot.serviceTags.forEach { tag ->
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
                
                // User quote
                scenicSpot.userQuote?.let { quote ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"$quote\"",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Ticket Price Section or Location Icon
            if (scenicSpot.ticketInfo != null) {
                ScenicSpotTicketSection(ticketInfo = scenicSpot.ticketInfo!!)
            } else {
                // Free scenic spot - show location icon
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "定位",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SpotLevelTag(spotLevel: String) {
    val (backgroundColor, textColor) = when (spotLevel) {
        "4A景区" -> Color(0xFFFF9800) to Color.White
        "红色景点" -> Color(0xFFFF5722) to Color.White
        "3A景区" -> Color(0xFF4CAF50) to Color.White
        "生态公园" -> Color(0xFF8BC34A) to Color.White
        else -> Color(0xFFF5F5F5) to Color.Gray
    }
    
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor
    ) {
        Text(
            text = spotLevel,
            fontSize = 10.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ScenicSpotTicketSection(
    ticketInfo: TicketInfo
) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        // Ticket description
        Text(
            text = ticketInfo.ticketDescription,
            fontSize = 11.sp,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Original price (strikethrough)
        ticketInfo.originalPrice?.let { originalPrice ->
            Text(
                text = originalPrice,
                fontSize = 12.sp,
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough
            )
        }
        
        // Current price
        Text(
            text = ticketInfo.currentPrice,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF5722)
        )
    }
}

private fun parseScenicSpotItem(jsonObject: JSONObject): ScenicSpotItem {
    val serviceTags = mutableListOf<String>()
    val serviceTagsArray = jsonObject.optJSONArray("serviceTags")
    if (serviceTagsArray != null) {
        for (i in 0 until serviceTagsArray.length()) {
            serviceTags.add(serviceTagsArray.getString(i))
        }
    }
    
    val ticketInfo = jsonObject.optJSONObject("ticketInfo")?.let { ticketJson ->
        TicketInfo(
            ticketDescription = ticketJson.getString("ticketDescription"),
            originalPrice = ticketJson.optString("originalPrice").takeIf { it.isNotEmpty() },
            currentPrice = ticketJson.getString("currentPrice")
        )
    }
    
    return ScenicSpotItem(
        id = jsonObject.getString("id"),
        spotName = jsonObject.getString("spotName"),
        spotLevel = jsonObject.getString("spotLevel"),
        address = jsonObject.getString("address"),
        latitude = jsonObject.getDouble("latitude"),
        longitude = jsonObject.getDouble("longitude"),
        distance = jsonObject.getString("distance"),
        travelTime = jsonObject.getString("travelTime"),
        rating = jsonObject.getDouble("rating").toFloat(),
        ratingText = jsonObject.getString("ratingText"),
        activityInfo = jsonObject.getString("activityInfo"),
        rankingInfo = jsonObject.optString("rankingInfo").takeIf { it.isNotEmpty() },
        serviceTags = serviceTags,
        userQuote = jsonObject.optString("userQuote").takeIf { it.isNotEmpty() },
        ticketInfo = ticketInfo,
        logo = jsonObject.optString("logo").takeIf { it.isNotEmpty() },
        phone = jsonObject.optString("phone").takeIf { it.isNotEmpty() }
    )
}