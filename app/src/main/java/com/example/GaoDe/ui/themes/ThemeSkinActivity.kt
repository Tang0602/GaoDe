package com.example.GaoDe.ui.themes

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle

data class ThemeSkin(
    val id: String,
    val name: String,
    val description: String,
    val price: String? = null,
    val isActive: Boolean = false,
    val isSpecial: Boolean = false,
    val backgroundColor: List<Color>,
    val icon: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSkinScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(ThemeTab.SKIN) }
    var selectedSkinId by remember { mutableStateOf("classic") }
    
    // 皮肤数据
    val themeSkins = remember {
        listOf(
            ThemeSkin(
                id = "classic",
                name = "经典主题",
                description = "高德经典主题，日夜守护你的每一次出行",
                isActive = true,
                backgroundColor = listOf(
                    Color(0xFF4A90E2),
                    Color(0xFF2E7CE6)
                ),
                icon = "🗺️"
            ),
            ThemeSkin(
                id = "melody",
                name = "美乐蒂主题皮肤",
                description = "美乐蒂主题皮肤上线，一起出门！",
                price = "¥9.9",
                isSpecial = true,
                backgroundColor = listOf(
                    Color(0xFFFF69B4),
                    Color(0xFFE91E63)
                ),
                icon = "🐰"
            ),
            ThemeSkin(
                id = "bear",
                name = "打盹熊皮肤",
                description = "可爱熊熊陪你出行",
                price = "¥12.9",
                backgroundColor = listOf(
                    Color(0xFFFFB74D),
                    Color(0xFFFF9800)
                ),
                icon = "🐻"
            )
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部导航栏
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.Black
                    )
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color.Black
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )
        
        // Tab选项卡
        ThemeTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        // 皮肤列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(themeSkins) { skin ->
                ThemeSkinCard(
                    skin = skin,
                    isSelected = selectedSkinId == skin.id,
                    onSkinSelected = { selectedSkinId = skin.id },
                    onApply = { /* 应用皮肤逻辑 */ },
                    onPurchase = { /* 购买皮肤逻辑 */ }
                )
            }
        }
    }
}

enum class ThemeTab {
    VOICE_PACK,
    CAR_ICON,
    SKIN,
    MELODY
}

@Composable
fun ThemeTabRow(
    selectedTab: ThemeTab,
    onTabSelected: (ThemeTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TabItem(
            text = "语音包",
            isSelected = selectedTab == ThemeTab.VOICE_PACK,
            onClick = { onTabSelected(ThemeTab.VOICE_PACK) }
        )
        TabItem(
            text = "车标",
            isSelected = selectedTab == ThemeTab.CAR_ICON,
            onClick = { onTabSelected(ThemeTab.CAR_ICON) }
        )
        TabItem(
            text = "皮肤",
            isSelected = selectedTab == ThemeTab.SKIN,
            onClick = { onTabSelected(ThemeTab.SKIN) }
        )
        TabItem(
            text = "美乐蒂",
            isSelected = selectedTab == ThemeTab.MELODY,
            onClick = { onTabSelected(ThemeTab.MELODY) }
        )
    }
}

@Composable
fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color.Gray
        )
        
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(2.dp)
                    .background(Color(0xFF2196F3))
            )
        }
    }
}

@Composable
fun ThemeSkinCard(
    skin: ThemeSkin,
    isSelected: Boolean,
    onSkinSelected: () -> Unit,
    onApply: () -> Unit,
    onPurchase: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSkinSelected() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 背景渐变
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = skin.backgroundColor
                        )
                    )
            )
            
            // 特殊标签
            if (skin.isSpecial) {
                Surface(
                    modifier = Modifier
                        .padding(12.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFFF5722)
                ) {
                    Text(
                        text = "套装",
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            // 主要内容
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // 图标和导航示意图
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = skin.icon,
                        fontSize = 48.sp
                    )
                    
                    // 模拟导航界面预览
                    Surface(
                        modifier = Modifier
                            .size(80.dp, 100.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.9f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🗺️",
                                fontSize = 24.sp
                            )
                            Text(
                                text = "导航",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 主题名称
                Text(
                    text = skin.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                // 描述
                Text(
                    text = skin.description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 按钮区域
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (skin.isActive) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "使用中",
                                fontSize = 14.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    } else if (skin.price != null) {
                        Button(
                            onClick = onPurchase,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = skin.price,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = onApply,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "应用",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Activity用于在Android中启动这个Screen
class ThemeSkinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemeSkinScreen(
                onNavigateBack = {
                    finish()
                }
            )
        }
    }
}