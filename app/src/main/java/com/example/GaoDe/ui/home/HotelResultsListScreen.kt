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
import com.example.GaoDe.model.HotelItem
import com.example.GaoDe.model.HotelPriceInfo
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelResultsListScreen(
    searchCategory: String = "酒店",
    onBackClick: () -> Unit = {},
    onHotelClick: (String) -> Unit = {}
) {
    var hotelList by remember { mutableStateOf<List<HotelItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("data/poi_hotels.json")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)
                val items = mutableListOf<HotelItem>()
                
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    items.add(parseHotelItem(jsonObject))
                }
                
                withContext(Dispatchers.Main) {
                    hotelList = items
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
        // Hotel Top Bar
        HotelTopBar(
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
            // Hotel List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(hotelList) { hotel ->
                    HotelListItem(
                        hotel = hotel,
                        onClick = { onHotelClick(hotel.id) }
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
fun HotelTopBar(
    searchCategory: String,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column {
            // First Row: Back button and date selection
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
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Date selection section
                Text(
                    text = "住 10-23 离 10-24",
                    fontSize = 16.sp,
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
            
            // Second Row: City and category
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    color = Color.Black
                )
            }
        }
    }
}


@Composable
fun HotelListItem(
    hotel: HotelItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var logoImageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    LaunchedEffect(hotel.logo) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("avatar/${hotel.logo}")
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
            // Hotel Image
            Box(
                modifier = Modifier.size(80.dp)
            ) {
                if (logoImageBitmap != null) {
                    Image(
                        bitmap = logoImageBitmap!!,
                        contentDescription = hotel.hotelName,
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
                                text = "🏨",
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Hotel Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Hotel name and type
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hotel.hotelName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Hotel type and metro info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hotel.hotelType,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    hotel.nearMetro?.let { metro ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• $metro",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    hotel.surroundingInfo?.let { info ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• $info",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Rating and ranking
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2196F3)
                    ) {
                        Text(
                            text = "${hotel.rating} ${hotel.ratingText}",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    
                    hotel.rankingInfo?.let { ranking ->
                        Spacer(modifier = Modifier.width(8.dp))
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
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Service tags
                FlowRow(
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 4.dp
                ) {
                    hotel.serviceTags.forEach { tag ->
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
                hotel.userQuote?.let { quote ->
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
            
            // Price Section
            HotelPriceSection(priceInfo = hotel.priceInfo)
        }
    }
}

@Composable
fun HotelPriceSection(
    priceInfo: HotelPriceInfo
) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        // Lowest price hint
        Text(
            text = priceInfo.lowestPriceHint,
            fontSize = 10.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Original price (strikethrough)
        priceInfo.originalPrice?.let { originalPrice ->
            Text(
                text = originalPrice,
                fontSize = 12.sp,
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough
            )
        }
        
        // Current price
        Text(
            text = priceInfo.currentPrice,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF5722)
        )
        
        // Discount info
        priceInfo.discountInfo?.let { discount ->
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFFF9800)
            ) {
                Text(
                    text = discount,
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

private fun parseHotelItem(jsonObject: JSONObject): HotelItem {
    val serviceTags = mutableListOf<String>()
    val serviceTagsArray = jsonObject.optJSONArray("serviceTags")
    if (serviceTagsArray != null) {
        for (i in 0 until serviceTagsArray.length()) {
            serviceTags.add(serviceTagsArray.getString(i))
        }
    }
    
    val priceInfoJson = jsonObject.getJSONObject("priceInfo")
    val priceInfo = HotelPriceInfo(
        lowestPriceHint = priceInfoJson.getString("lowestPriceHint"),
        originalPrice = priceInfoJson.optString("originalPrice").takeIf { it.isNotEmpty() },
        currentPrice = priceInfoJson.getString("currentPrice"),
        discountInfo = priceInfoJson.optString("discountInfo").takeIf { it.isNotEmpty() }
    )
    
    return HotelItem(
        id = jsonObject.getString("id"),
        hotelName = jsonObject.getString("hotelName"),
        hotelType = jsonObject.getString("hotelType"),
        address = jsonObject.getString("address"),
        latitude = jsonObject.getDouble("latitude"),
        longitude = jsonObject.getDouble("longitude"),
        nearMetro = jsonObject.optString("nearMetro").takeIf { it.isNotEmpty() },
        surroundingInfo = jsonObject.optString("surroundingInfo").takeIf { it.isNotEmpty() },
        rating = jsonObject.getDouble("rating").toFloat(),
        ratingText = jsonObject.getString("ratingText"),
        rankingInfo = jsonObject.optString("rankingInfo").takeIf { it.isNotEmpty() },
        serviceTags = serviceTags,
        userQuote = jsonObject.optString("userQuote").takeIf { it.isNotEmpty() },
        priceInfo = priceInfo,
        logo = jsonObject.optString("logo").takeIf { it.isNotEmpty() },
        phone = jsonObject.optString("phone").takeIf { it.isNotEmpty() }
    )
}