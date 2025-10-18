package com.example.GaoDe.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
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
    
    val bottomSheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    
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
    
    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetPeekHeight = 80.dp,
        sheetContent = {
            SheetContent(
                searchQuery = searchQuery,
                onSearchQueryChange = { 
                    searchQuery = it
                    presenter.searchPlaces(it)
                },
                onPlaceClick = { presenter.onPlaceClicked(it) }
            )
        }
    ) {
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
}

@Composable
fun MapView(
    modifier: Modifier = Modifier,
    places: List<Place>
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        /* Map SDK View Placeholder */
        // TODO: Replace with actual map SDK implementation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD),
                            Color(0xFFBBDEFB)
                        )
                    )
                )
        )
        
        // Map overlay elements
        Text(
            text = "地图加载中...",
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    Color.White.copy(alpha = 0.9f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.8f)
        )
        
        // User location indicator
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 40.dp)
                .size(16.dp)
                .background(Color(0xFF2196F3), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.White, CircleShape)
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FloatingActionButton(
            onClick = { },
            modifier = Modifier
                .size(52.dp)
                .graphicsLayer {
                    shadowElevation = 8.dp.toPx()
                    shape = CircleShape
                    clip = true
                },
            containerColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Icon(
                Icons.Default.Add, 
                contentDescription = "更多",
                tint = Color.Gray.copy(alpha = 0.8f)
            )
        }
        
        FloatingActionButton(
            onClick = { },
            modifier = Modifier
                .size(52.dp)
                .graphicsLayer {
                    shadowElevation = 8.dp.toPx()
                    shape = CircleShape
                    clip = true
                },
            containerColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Icon(
                Icons.Default.Layers, 
                contentDescription = "图层",
                tint = Color.Gray.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPlaceClick: (Place) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(5.dp)
                .background(
                    Color.Gray.copy(alpha = 0.4f),
                    RoundedCornerShape(3.dp)
                )
                .align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        InlineSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        QuickActionsGrid()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        HomeWorkButtons()
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun InlineSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "搜索",
                tint = Color.Gray.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.87f)
                ),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "武汉站",
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )
            
        }
    }
}

@Composable
fun QuickActionsGrid() {
    val actions = listOf(
        QuickAction("🚌", stringResource(R.string.public_transport)),
        QuickAction("🚇", "地铁"),
        QuickAction("🚴", stringResource(R.string.cycling)),
        QuickAction("🚗", stringResource(R.string.taxi)),
        QuickAction("🚶", stringResource(R.string.walking)),
        QuickAction("🏨", stringResource(R.string.hotel))
    )
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        modifier = Modifier.height(120.dp)
    ) {
        items(actions) { action ->
            QuickActionItem(action = action)
        }
    }
}

data class QuickAction(
    val icon: String,
    val label: String
)

@Composable
fun QuickActionItem(action: QuickAction) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            modifier = Modifier.size(54.dp),
            shape = CircleShape,
            color = Color(0xFFE8F5E9),
            shadowElevation = 2.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = action.icon,
                    fontSize = 24.sp
                )
            }
        }
        
        Text(
            text = action.label,
            fontSize = 11.sp,
            color = Color.Gray.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun HomeWorkButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "回家",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "回家",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.87f)
                )
            }
        }
        
        Surface(
            modifier = Modifier.weight(1f),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Business,
                    contentDescription = "去单位",
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "去单位",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.87f)
                )
            }
        }
    }
}

