package com.example.GaoDe.ui.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent

@Composable
fun MyScreen() {
    val context = LocalContext.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 主要内容区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 顶部用户信息模块
            UserProfileHeader()
            
            // 个性化设置模块
            PersonalizationCard()
            
            // 订单中心模块
            OrderCenterCard(
                onAllOrdersClick = {
                    val intent = Intent(context, com.example.GaoDe.ui.orders.OrderListActivity::class.java)
                    context.startActivity(intent)
                }
            )
            
            // 常用工具模块
            CommonToolsCard(
                onFavoriteClick = {
                    val intent = Intent(context, com.example.GaoDe.ui.favorites.FavoritePlacesActivity::class.java)
                    context.startActivity(intent)
                }
            )
            
            // 我的车辆模块
            MyVehicleCard()
            
            // 底部空白，避免被底部导航栏遮挡
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        // 右上角浮动按钮
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.size(48.dp),
                containerColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp
                )
            ) {
                Icon(
                    Icons.Default.List,
                    contentDescription = "列表",
                    tint = Color.Gray
                )
            }
            
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.size(48.dp),
                containerColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp
                )
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "设置",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun UserProfileHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
            // 用户头像
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6B9EFF))
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "用户头像",
                    tint = Color.White,
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 用户信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "amap_bo0r19TijK",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2196F3)
                ) {
                    Text(
                        text = "可成为 Lv.2 高德达人",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "0 粉丝 0 关注 0 贡献",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // 主页链接
            Text(
                text = "主页 >",
                fontSize = 14.sp,
                color = Color.Gray
            )
            }
        }
    }
}


@Composable
fun PersonalizationCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 语音包
            PersonalizationItem(
                icon = Icons.Default.Person,
                iconColor = Color(0xFFE91E63),
                title = "语音包",
                subtitle = "林志玲",
                modifier = Modifier.weight(1f)
            )
            
            // 车标
            PersonalizationItem(
                icon = Icons.Default.DirectionsCar,
                iconColor = Color(0xFF2196F3),
                title = "车标",
                subtitle = "3D跑车",
                modifier = Modifier.weight(1f)
            )
            
            // 皮肤
            PersonalizationItem(
                icon = Icons.Default.Palette,
                iconColor = Color(0xFF4CAF50),
                title = "皮肤",
                subtitle = "经典主题",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PersonalizationItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun OrderCenterCard(
    onAllOrdersClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "订单中心",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OrderItem(
                    icon = Icons.Default.Assignment,
                    title = "全部订单",
                    onClick = onAllOrdersClick,
                    modifier = Modifier.weight(1f)
                )
                
                OrderItem(
                    icon = Icons.Default.Cancel,
                    title = "退款/取消",
                    modifier = Modifier.weight(1f)
                )
                
                OrderItem(
                    icon = Icons.Default.Star,
                    title = "待评价",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun OrderItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun CommonToolsCard(
    onFavoriteClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                ToolItem(
                    icon = Icons.Default.Star,
                    iconColor = Color(0xFFFF9800),
                    title = "收藏夹",
                    onClick = onFavoriteClick,
                    modifier = Modifier.weight(1f)
                )
                
                ToolItem(
                    icon = Icons.Default.VolumeUp,
                    iconColor = Color(0xFF2196F3),
                    title = "语音包",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ToolItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}


@Composable
fun MyVehicleCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "我的车辆",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2196F3)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "添加车辆",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "添加爱车享权益",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "添加爱车点这里",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "进入",
                    tint = Color.Gray
                )
            }
        }
    }
}