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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.BitmapFactory
import com.example.GaoDe.model.POIItem
import com.example.GaoDe.model.GroupBuyInfo
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun POIResultsListScreen(
    searchCategory: String = "美食",
    onBackClick: () -> Unit = {},
    onPOIClick: (String) -> Unit = {}
) {
    var poiList by remember { mutableStateOf<List<POIItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("data/poi_restaurants.json")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)
                val items = mutableListOf<POIItem>()
                
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    items.add(parsePOIItem(jsonObject))
                }
                
                withContext(Dispatchers.Main) {
                    poiList = items
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
        POITopBar(
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
            // POI List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(poiList) { poi ->
                    POIListItem(
                        poi = poi,
                        onClick = { onPOIClick(poi.id) }
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
fun POITopBar(
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
            
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF5F5F5)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = searchCategory,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "定位",
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
fun POIListItem(
    poi: POIItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var logoImageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    LaunchedEffect(poi.logo) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("avatar/${poi.logo}")
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Logo Section
            Box(
                modifier = Modifier.size(80.dp)
            ) {
                if (logoImageBitmap != null) {
                    Image(
                        bitmap = logoImageBitmap!!,
                        contentDescription = poi.brandName,
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
                                text = "🍽️",
                                fontSize = 24.sp
                            )
                        }
                    }
                }
                
                // Top certification tags
                if (poi.certificationTags.contains("高德甄选")) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset((-4).dp, (-4).dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFF9800)
                    ) {
                        Text(
                            text = "高德甄选",
                            fontSize = 8.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                
                // Bottom status
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 4.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = poi.operatingStatus,
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Details Section
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Brand name and verification
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = poi.brandName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (poi.verified) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "已认证",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Distance and travel time
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = poi.travelTime,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = poi.distance,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Rating section
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2196F3)
                    ) {
                        Text(
                            text = "${poi.rating} ${poi.ratingText}",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${poi.category} ${poi.pricePerPerson} ${poi.viewCount}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Ranking and specialties
                FlowRow(
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 4.dp
                ) {
                    poi.rankingInfo?.let { ranking ->
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
                    
                    poi.specialties.forEach { specialty ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF5F5F5)
                        ) {
                            Text(
                                text = specialty,
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                // User quote
                poi.userQuote?.let { quote ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"$quote\"",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Promotion info
                poi.promotionInfo?.let { promo ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = promo,
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50)
                    )
                }
                
                // Group buy info
                poi.groupBuyInfo?.let { groupBuy ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFF9800)
                        ) {
                            Text(
                                text = "团",
                                fontSize = 10.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = groupBuy.currentPrice,
                            fontSize = 14.sp,
                            color = Color(0xFFFF5722),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = groupBuy.originalPrice,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5722))
                        ) {
                            Text(
                                text = groupBuy.discount,
                                fontSize = 10.sp,
                                color = Color(0xFFFF5722),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = groupBuy.description,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Action button
            Surface(
                modifier = Modifier.clickable { },
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF2196F3)
            ) {
                Text(
                    text = poi.actionButtonText,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private fun parsePOIItem(jsonObject: JSONObject): POIItem {
    val certificationTags = mutableListOf<String>()
    val certTagsArray = jsonObject.optJSONArray("certificationTags")
    if (certTagsArray != null) {
        for (i in 0 until certTagsArray.length()) {
            certificationTags.add(certTagsArray.getString(i))
        }
    }
    
    val specialties = mutableListOf<String>()
    val specialtiesArray = jsonObject.optJSONArray("specialties")
    if (specialtiesArray != null) {
        for (i in 0 until specialtiesArray.length()) {
            specialties.add(specialtiesArray.getString(i))
        }
    }
    
    val groupBuyInfo = jsonObject.optJSONObject("groupBuyInfo")?.let { groupBuyJson ->
        GroupBuyInfo(
            currentPrice = groupBuyJson.getString("currentPrice"),
            originalPrice = groupBuyJson.getString("originalPrice"),
            discount = groupBuyJson.getString("discount"),
            description = groupBuyJson.getString("description")
        )
    }
    
    return POIItem(
        id = jsonObject.getString("id"),
        brandName = jsonObject.getString("brandName"),
        address = jsonObject.getString("address"),
        latitude = jsonObject.getDouble("latitude"),
        longitude = jsonObject.getDouble("longitude"),
        category = jsonObject.getString("category"),
        phone = jsonObject.optString("phone"),
        rating = jsonObject.getDouble("rating").toFloat(),
        ratingText = jsonObject.getString("ratingText"),
        pricePerPerson = jsonObject.getString("pricePerPerson"),
        viewCount = jsonObject.getString("viewCount"),
        logo = jsonObject.getString("logo"),
        verified = jsonObject.getBoolean("verified"),
        operatingStatus = jsonObject.getString("operatingStatus"),
        certificationTags = certificationTags,
        distance = jsonObject.getString("distance"),
        travelTime = jsonObject.getString("travelTime"),
        rankingInfo = jsonObject.optString("rankingInfo").takeIf { it.isNotEmpty() },
        specialties = specialties,
        userQuote = jsonObject.optString("userQuote").takeIf { it.isNotEmpty() },
        promotionInfo = jsonObject.optString("promotionInfo").takeIf { it.isNotEmpty() },
        groupBuyInfo = groupBuyInfo,
        actionButtonText = jsonObject.getString("actionButtonText")
    )
}