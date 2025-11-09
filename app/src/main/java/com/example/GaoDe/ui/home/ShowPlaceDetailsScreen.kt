package com.example.GaoDe.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.BitmapFactory
import com.example.GaoDe.data.DataManager
import com.example.GaoDe.model.PlaceDetails
import com.google.accompanist.flowlayout.FlowRow
import com.example.GaoDe.ui.home.ShowPlaceDetailsContract
import com.example.GaoDe.ui.home.ShowPlaceDetailsPresenter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// --- 修改后 ---
fun ShowPlaceDetailsScreen(
    placeId: String,
    onBackClick: () -> Unit = {},
    onRouteClick: (String) -> Unit = {},
    onShareClick: (String, String) -> Unit = { _, _ -> },
    onFavoriteClick: (String) -> Unit = {}
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
                    onRouteClick = onRouteClick,
                    onShareClick = onShareClick,
                    onFavoriteClick = onFavoriteClick
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
        TransportInfoSection(placeDetails)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Address Section
        AddressSection(placeDetails.place.address, placeDetails.place.phone)
        
        // Menu Section (only for 巴奴火锅)
        if (placeDetails.place.id == "place_006") {
            Spacer(modifier = Modifier.height(24.dp))
            MenuSection(placeDetails.photos)
        }
        
        // Reviews Section (only for 巴奴火锅)
        if (placeDetails.place.id == "place_006") {
            Spacer(modifier = Modifier.height(24.dp))
            ReviewsSection(placeDetails.reviews)
        }
        
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
            
            // 只为有价格的地点显示价格
            when (placeDetails.place.id) {
                "place_006" -> {
                    Text(
                        text = "人均：¥148/人",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                "place_007" -> {
                    Text(
                        text = "人均：¥85/人",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                "place_008" -> {
                    Text(
                        text = "人均：¥35/人",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                "place_011" -> {
                    Text(
                        text = "房间：¥188起",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                "place_012" -> {
                    Text(
                        text = "房间：¥968起",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                "place_013" -> {
                    Text(
                        text = "房间：¥148起",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                "place_014" -> {
                    Text(
                        text = "房间：¥1388起",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                "place_015" -> {
                    Text(
                        text = "门票：¥229起",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                "place_016" -> {
                    Text(
                        text = "门票：¥25起",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                "place_017" -> {
                    Text(
                        text = "门票：¥70起",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                "place_018" -> {
                    // 南湖花溪公园免费，不显示价格
                }
                "place_019" -> {
                    Text(
                        text = "门票：¥168起",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                else -> {
                    Text(
                        text = "欢迎咨询",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
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
fun TransportInfoSection(placeDetails: PlaceDetails) {
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
            text = when (placeDetails.place.id) {
                "place_006" -> "驾车 4.2公里 16分钟"
                "place_007" -> "步行 890米 12分钟"
                "place_008" -> "驾车 3.5公里 8分钟"
                "place_009" -> "驾车 2.1公里 12分钟"
                "place_010" -> "驾车 6.8公里 15分钟"
                "place_011" -> "驾车 2.8公里 9分钟"
                "place_012" -> "驾车 9.2公里 22分钟"
                "place_013" -> "驾车 7.2公里 18分钟"
                "place_014" -> "驾车 5.1公里 12分钟"
                "place_015" -> "驾车 13.1公里 25分钟"
                "place_016" -> "驾车 8.5公里 18分钟"
                "place_017" -> "驾车 6.2公里 15分钟"
                "place_018" -> "步行 5.8公里 12分钟"
                "place_019" -> "驾车 18.6公里 35分钟"
                else -> "驾车 5.0公里 15分钟"
            },
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
                        text = "商区位置 >",
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
fun MenuSection(photos: List<String>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "菜品",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "查看全部",
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
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "菜单 (2)",
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val dishes = listOf(
                "经典毛肚" to "经典毛肚.jpg",
                "梅花肉" to "梅花肉.jpg"
            )
            
            items(dishes) { (dishName, imageName) ->
                DishItem(dishName = dishName, imageName = imageName)
            }
        }
    }
}

@Composable
fun DishItem(dishName: String, imageName: String) {
    val context = LocalContext.current
    val bitmap = remember(imageName) {
        try {
            val inputStream = context.assets.open("avatar/$imageName")
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF5F5F5)
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = dishName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "🍲",
                        fontSize = 24.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = dishName,
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}

@Composable
fun ReviewsSection(reviews: List<com.example.GaoDe.model.Review>) {
    Column {
        Text(
            text = "用户评价 (52)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            val reviewTags = listOf(
                "本地人评价 6",
                "导航/现场评价 9", 
                "服务优质 15",
                "菜品美味 10",
                "排队人少 2",
                "价格合理 1",
                "价格高 2"
            )
            
            reviewTags.forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Text(
                        text = tag,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (reviews.isNotEmpty()) {
            ReviewCard(review = reviews.first())
        }
    }
}

@Composable
fun ReviewCard(review: com.example.GaoDe.model.Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFFF5F5F5)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = review.userName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFF9800)
                        ) {
                            Text(
                                text = "高德达人",
                                fontSize = 10.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "2025-10-07 · 高德地图",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${review.rating.toInt()}分",
                            fontSize = 14.sp,
                            color = Color(0xFF2196F3),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "现场拍摄",
                                fontSize = 12.sp,
                                color = Color(0xFF2196F3)
                            )
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = review.comment,
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Text(
                            text = "全文",
                            fontSize = 12.sp,
                            color = Color(0xFF2196F3),
                            modifier = Modifier.clickable { }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "浏览 377",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun BottomActionBar(
    placeDetails: PlaceDetails,
    onRouteClick: (String) -> Unit,
    onShareClick: (String, String) -> Unit = { _, _ -> },
    onFavoriteClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val dataManager = remember { DataManager(context) }
    var isFavorited by remember { mutableStateOf(dataManager.isPlaceFavorited(placeDetails.place.id)) }
    var favoriteCount by remember { mutableIntStateOf(dataManager.getFavorites().size) }
    var showShareDialog by remember { mutableStateOf(false) }

    val toggleFavorite: (PlaceDetails) -> Unit = { details ->
        onFavoriteClick(details.place.id)

        if (isFavorited) {
            if (dataManager.removeFromFavorites(details.place.id)) {
                isFavorited = false
                favoriteCount = dataManager.getFavorites().size
            }
        } else {
            if (dataManager.addToFavorites(details.place)) {
                isFavorited = true
                favoriteCount = dataManager.getFavorites().size
            }
        }
    }
    
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
                    icon = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    text = "收藏",
                    badge = favoriteCount.toString(),
                    isSelected = isFavorited,
                    onClick = { toggleFavorite(placeDetails) }
                )
                ActionButton(
                    icon = Icons.Default.Share,
                    text = "分享",
                    onClick = { showShareDialog = true }
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
    
    // 分享联系人选择对话框
    if (showShareDialog) {
        ShareContactDialog(
            placeName = placeDetails.place.name,
            onDismiss = { showShareDialog = false },
            onShareToContact = { contactId, contactName ->
                onShareClick(contactId, "${placeDetails.place.name} - ${placeDetails.place.address}")
                showShareDialog = false
            }
        )
    }
}

@Composable
fun ShareContactDialog(
    placeName: String,
    onDismiss: () -> Unit,
    onShareToContact: (String, String) -> Unit
) {
    val context = LocalContext.current
    
    // 联系人数据
    val contacts = listOf(
        "dad" to Pair("爸爸", "friend_1.jpg"),
        "mom" to Pair("妈妈", "friend_2.jpg")
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "分享地点",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Column {
                Text(
                    text = "将「$placeName」分享给：",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 联系人列表
                contacts.forEach { (contactId, contactInfo) ->
                    val (contactName, avatarFileName) = contactInfo
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onShareToContact(contactId, contactName) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 头像
                        val bitmap = remember(avatarFileName) {
                            try {
                                val inputStream = context.assets.open("avatar/$avatarFileName")
                                BitmapFactory.decodeStream(inputStream)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF5F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = contactName,
                                    modifier = Modifier.size(40.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = contactName,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "取消",
                    color = Color.Gray
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    badge: String? = null,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box {
            Icon(
                icon,
                contentDescription = text,
                tint = if (isSelected) Color(0xFFFF5722) else Color.Gray,
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
            color = if (isSelected) Color(0xFFFF5722) else Color.Gray
        )
    }
}