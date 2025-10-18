package com.example.GaoDe.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.GaoDe.R
import com.example.GaoDe.data.DataManager
import com.example.GaoDe.model.Place

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val dataManager = remember { DataManager(context) }
    val presenter = remember { HomePresenter(dataManager) }
    
    var places by remember { mutableStateOf<List<Place>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    val homeView = remember {
        object : HomeContract.View {
            override fun showNearbyPlaces(nearbyPlaces: List<Place>) {
                places = nearbyPlaces
            }
            
            override fun showSearchResults(searchResults: List<Place>) {
                places = searchResults
            }
            
            override fun navigateToPlaceDetails(place: Place) {
                
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
        }
    }
    
    LaunchedEffect(Unit) {
        presenter.attachView(homeView)
        presenter.loadNearbyPlaces()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            presenter.detachView()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        MapView(
            modifier = Modifier.fillMaxSize(),
            places = places
        )
        
        MapControls(
            modifier = Modifier.align(Alignment.CenterEnd),
            onLocationClick = { presenter.onLocationButtonClicked() },
            onRouteClick = { presenter.onRouteButtonClicked() }
        )
        
        BottomSheet(
            modifier = Modifier.align(Alignment.BottomCenter),
            searchQuery = searchQuery,
            onSearchQueryChange = { 
                searchQuery = it
                presenter.searchPlaces(it)
            },
            onPlaceClick = { presenter.onPlaceClicked(it) }
        )
        
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        errorMessage?.let { message ->
            LaunchedEffect(message) {
                
                errorMessage = null
            }
        }
    }
}

@Composable
fun MapView(
    modifier: Modifier = Modifier,
    places: List<Place>
) {
    Box(
        modifier = modifier
            .background(Color(0xFFE8F5E8))
    ) {
        Text(
            text = "地图视图 - ${places.size} 个地点",
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    Color.White.copy(alpha = 0.8f),
                    RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(40.dp)
                .background(Color.Blue.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "用户位置",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun MapControls(
    modifier: Modifier = Modifier,
    onLocationClick: () -> Unit,
    onRouteClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(
            onClick = { },
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Icon(Icons.Default.Add, contentDescription = "更多")
        }
        
        FloatingActionButton(
            onClick = { },
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text("🗂️", fontSize = 20.sp)
        }
        
        FloatingActionButton(
            onClick = onLocationClick,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "定位")
        }
        
        FloatingActionButton(
            onClick = onRouteClick,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text("🧭", fontSize = 20.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPlaceClick: (Place) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
                    .align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = { 
                    Icon(Icons.Default.Mic, contentDescription = "语音搜索", tint = Color.Blue) 
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            QuickActionsGrid()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            RecommendationCards()
        }
    }
}

@Composable
fun QuickActionsGrid() {
    val actions = listOf(
        QuickAction("🚌", stringResource(R.string.public_transport)),
        QuickAction("🚍", stringResource(R.string.real_time_bus)),
        QuickAction("🚴", stringResource(R.string.cycling)),
        QuickAction("🚗", stringResource(R.string.taxi)),
        QuickAction("🏨", stringResource(R.string.hotel), hasTag = true),
        QuickAction("🚄", stringResource(R.string.train_flight)),
        QuickAction("🚙", stringResource(R.string.carpool)),
        QuickAction("🚶", stringResource(R.string.walking)),
        QuickAction("🎯", stringResource(R.string.nearby_tour)),
        QuickAction("⚙️", stringResource(R.string.more_tools))
    )
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.height(120.dp)
    ) {
        items(actions) { action ->
            QuickActionItem(action = action)
        }
    }
}

data class QuickAction(
    val icon: String,
    val label: String,
    val hasTag: Boolean = false
)

@Composable
fun QuickActionItem(action: QuickAction) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Green.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = action.icon,
                    fontSize = 20.sp
                )
            }
            
            if (action.hasTag) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.Red, RoundedCornerShape(6.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "订周末",
                        color = Color.White,
                        fontSize = 8.sp
                    )
                }
            }
        }
        
        Text(
            text = action.label,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun RecommendationCards() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Red, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "优惠",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "打车去南湖山庄",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "· 半岛花园",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "预计3分钟接驾",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Close, contentDescription = "关闭")
                }
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "周边公共交通推荐",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = ">",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🚌",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "586路",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "20分钟/趟",
                            color = Color.Green,
                            fontSize = 12.sp
                        )
                    }
                }
                
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "关注",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}