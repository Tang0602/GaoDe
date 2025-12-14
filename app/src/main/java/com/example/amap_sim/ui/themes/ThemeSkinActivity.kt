package com.example.amap_sim.ui.themes

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
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
    val backgroundColor: List<Color>,
    val icon: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSkinScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedSkinId by remember { mutableStateOf("classic") }
    var activeSkinId by remember { mutableStateOf("classic") }
    var purchasedSkins by remember { mutableStateOf(setOf("classic")) }
    var showPurchaseDialog by remember { mutableStateOf(false) }
    var purchasingSkinId by remember { mutableStateOf("") }
    
    // 简化的皮肤数据
    val themeSkins = remember {
        listOf(
            ThemeSkin(
                id = "classic",
                name = "经典主题",
                description = "默认主题",
                backgroundColor = listOf(Color(0xFF2196F3)),
                icon = "🗺️"
            ),
            ThemeSkin(
                id = "melody",
                name = "美乐蒂",
                description = "粉色可爱主题",
                price = "¥9.9",
                backgroundColor = listOf(Color(0xFFFF69B4)),
                icon = "🐰"
            ),
            ThemeSkin(
                id = "bear",
                name = "打盹熊",
                description = "橙色温暖主题",
                price = "¥12.9",
                backgroundColor = listOf(Color(0xFFFF9800)),
                icon = "🐻"
            )
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 简化的顶部导航栏
        TopAppBar(
            title = { 
                Text(
                    text = "皮肤设置",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
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
        
        // 简化的皮肤列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(themeSkins) { skin ->
                SimpleSkinCard(
                    skin = skin,
                    isSelected = selectedSkinId == skin.id,
                    isActive = activeSkinId == skin.id,
                    isPurchased = purchasedSkins.contains(skin.id),
                    onSkinSelected = { selectedSkinId = skin.id },
                    onPurchase = { 
                        purchasingSkinId = skin.id
                        showPurchaseDialog = true 
                    },
                    onApply = { 
                        activeSkinId = skin.id
                    }
                )
            }
        }
    }
    
    // 购买成功对话框
    if (showPurchaseDialog) {
        PurchaseSuccessDialog(
            skinName = themeSkins.find { it.id == purchasingSkinId }?.name ?: "",
            onConfirm = { 
                showPurchaseDialog = false
                purchasingSkinId = ""
            },
            onApplyImmediately = {
                purchasedSkins = purchasedSkins + purchasingSkinId
                activeSkinId = purchasingSkinId
                showPurchaseDialog = false
                purchasingSkinId = ""
            }
        )
    }
}


@Composable
fun SimpleSkinCard(
    skin: ThemeSkin,
    isSelected: Boolean,
    isActive: Boolean,
    isPurchased: Boolean,
    onSkinSelected: () -> Unit,
    onPurchase: () -> Unit = {},
    onApply: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSkinSelected() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF0F8FF) else Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF2196F3)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 皮肤图标和颜色预览
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = skin.backgroundColor.first()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = skin.icon,
                        fontSize = 24.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 皮肤信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = skin.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = skin.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // 状态或操作按钮
            when {
                isActive -> {
                    Text(
                        text = "使用中",
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
                !isPurchased && skin.price != null -> {
                    Text(
                        text = skin.price,
                        fontSize = 14.sp,
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onPurchase() }
                    )
                }
                isPurchased -> {
                    Button(
                        onClick = onApply,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "使用",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
                else -> {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun PurchaseSuccessDialog(
    skinName: String,
    onConfirm: () -> Unit,
    onApplyImmediately: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onConfirm,
        title = {
            Text(
                text = "购买成功",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Column {
                Text(
                    text = "恭喜您成功购买了「$skinName」皮肤！",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "您可以立即切换使用，或稍后在皮肤列表中切换。",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "确定",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }
                Button(
                    onClick = onApplyImmediately,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "立即切换",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
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