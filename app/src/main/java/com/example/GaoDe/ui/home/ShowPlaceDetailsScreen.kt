package com.example.GaoDe.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.GaoDe.data.DataManager
import com.example.GaoDe.model.PlaceDetails
import com.google.accompanist.flowlayout.FlowRow
import com.example.GaoDe.ui.home.ShowPlaceDetailsContract
import com.example.GaoDe.ui.home.ShowPlaceDetailsPresenter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowPlaceDetailsScreen(
    placeId: String,
    onBackClick: () -> Unit = {},
    onRouteClick: (String) -> Unit = {}
) {
    var placeDetails by remember { mutableStateOf<PlaceDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val dataManager = remember { DataManager(context) }
    
    val presenter = remember {
        ShowPlaceDetailsPresenter(
            view = object : ShowPlaceDetailsContract.View {
                override fun showPlaceDetails(details: PlaceDetails) {
                    placeDetails = details
                }
                
                override fun showLoading() {
                    isLoading = true
                }
                
                override fun hideLoading() {
                    isLoading = false
                }
                
                override fun showError(message: String) {
                    errorMessage = message
                }
                
                override fun setPresenter(presenter: ShowPlaceDetailsContract.Presenter) {
                    // No-op for Compose
                }
            },
            dataManager = dataManager
        )
    }
    
    LaunchedEffect(placeId) {
        presenter.start()
        presenter.loadPlaceDetails(placeId)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            presenter.stop()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top App Bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2196F3))
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "未知错误",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }
            placeDetails != null -> {
                PlaceDetailsContent(
                    placeDetails = placeDetails!!,
                    modifier = Modifier.weight(1f)
                )
                
                // Bottom Action Bar
                BottomActionBar(
                    placeDetails = placeDetails!!,
                    onRouteClick = onRouteClick
                )
            }
        }
    }
}

@Composable
fun PlaceDetailsContent(
    placeDetails: PlaceDetails,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        // Main Info Section
        PlaceMainInfo(placeDetails)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Service Tags
        ServiceTagsSection(placeDetails.facilities)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Business Hours
        BusinessHoursSection(placeDetails.businessHours ?: "")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Transport Info
        TransportInfoSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Address Section
        AddressSection(placeDetails.place.address, placeDetails.place.phone)
        
        Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom bar
    }
}

@Composable
fun PlaceMainInfo(placeDetails: PlaceDetails) {
    Column {
        // Title
        Text(
            text = placeDetails.place.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Tags Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Special Tag
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFFFC107),
                modifier = Modifier.border(1.dp, Color(0xFFFFC107), RoundedCornerShape(4.dp))
            ) {
                Text(
                    text = "连续 2年 >",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            
            // Category Tag
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFF5F5F5)
            ) {
                Text(
                    text = placeDetails.place.category ?: "其他",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Rating and Price Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF2196F3)
            ) {
                Text(
                    text = "${placeDetails.place.rating} 超棒",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            
            Text(
                text = "52 评价",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Text(
                text = when (placeDetails.place.category) {
                    "酒店" -> "价格详询"
                    "景点" -> "门票详询"
                    else -> "人均：¥148/人"
                },
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ServiceTagsSection(facilities: List<String>) {
    FlowRow(
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp
    ) {
        facilities.forEach { facility ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (facility == "街道口火锅榜") Color(0xFFFF5722) else Color(0xFFF5F5F5)
            ) {
                Text(
                    text = facility,
                    fontSize = 12.sp,
                    color = if (facility == "街道口火锅榜") Color.White else Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BusinessHoursSection(businessHours: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "营业中",
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = businessHours,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "详情",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun TransportInfoSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Default.DirectionsCar,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "驾车 4.2公里 16分钟",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun AddressSection(address: String, phone: String?) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Text(
                            text = "商场",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "群光广场 >",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            if (phone != null) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF5F5F5),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "电话",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BottomActionBar(
    placeDetails: PlaceDetails,
    onRouteClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Regular Functions
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ActionButton(
                    icon = Icons.Default.Favorite,
                    text = "收藏",
                    badge = "222"
                )
                ActionButton(
                    icon = Icons.Default.Share,
                    text = "分享"
                )
                ActionButton(
                    icon = Icons.Default.DirectionsCar,
                    text = "打车"
                )
            }
            
            // Main CTAs
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "导航",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }
                
                Button(
                    onClick = { 
                        onRouteClick(placeDetails.place.name)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "路线",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    badge: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { }
    ) {
        Box {
            Icon(
                icon,
                contentDescription = text,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            
            if (badge != null) {
                Surface(
                    modifier = Modifier.offset(x = 12.dp, y = (-4).dp),
                    shape = CircleShape,
                    color = Color.Red
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}