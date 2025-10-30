package com.example.GaoDe.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.GaoDe.model.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelledOrdersScreen(
    onNavigateBack: () -> Unit = {},
    onOrderClick: (Order) -> Unit = {}
) {
    // 模拟已取消的订单数据
    val cancelledOrders = remember {
        listOf(
            Order(
                id = "cancelled_001",
                orderType = OrderType.TAXI,
                orderTitle = "聚的出租车",
                status = OrderStatus.CANCELLED,
                price = 18.50,
                createdAt = System.currentTimeMillis() - 6 * 60 * 60 * 1000, // 6小时前
                startLocation = "武汉火车站",
                endLocation = "东湖风景区",
                isRealTime = true,
                cancelReason = "个人原因取消"
            ),
            Order(
                id = "cancelled_002", 
                orderType = OrderType.TAXI,
                orderTitle = "风韵特选经济型",
                status = OrderStatus.CANCELLED,
                price = 12.30,
                createdAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000, // 2天前
                startLocation = "光谷广场",
                endLocation = "汉庭酒店",
                isRealTime = false,
                cancelReason = "系统自动取消"
            ),
            Order(
                id = "cancelled_003",
                orderType = OrderType.TAXI,
                orderTitle = "及时特选经济型",
                status = OrderStatus.CANCELLED,
                price = 25.80,
                createdAt = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000, // 3天前
                startLocation = "黄鹤楼",
                endLocation = "武汉欢乐谷",
                isRealTime = true,
                cancelReason = "找不到合适车辆"
            )
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部导航栏
        TopAppBar(
            title = {
                Text(
                    text = "退款/取消",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
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
            actions = {
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
        
        // 统计信息
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color(0xFFF8F8F8),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "共${cancelledOrders.size}笔已取消订单",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "退款金额：¥${String.format("%.2f", cancelledOrders.sumOf { it.price })}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = "取消订单",
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        
        // 已取消订单列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = rememberLazyListState(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(cancelledOrders) { order ->
                CancelledOrderCard(
                    order = order,
                    onOrderClick = { onOrderClick(order) }
                )
                HorizontalDivider(
                    color = Color(0xFFF0F0F0),
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
fun CancelledOrderCard(
    order: Order,
    onOrderClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }
    val formattedTime = dateFormat.format(Date(order.createdAt))
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOrderClick() }
            .padding(16.dp)
    ) {
        // 订单类型、状态和价格行
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 订单类型图标和名称
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderType.icon,
                    fontSize = 20.sp
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = order.orderTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            // 实时标签
            if (order.isRealTime) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Text(
                        text = "实时",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 订单状态
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFFFEBEE)
            ) {
                Text(
                    text = "已取消",
                    fontSize = 12.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 退款金额
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "¥${String.format("%.2f", order.price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "已退款",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 时间和取消原因
        Text(
            text = "取消时间：$formattedTime",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        order.cancelReason?.let { reason ->
            Text(
                text = "取消原因：$reason",
                fontSize = 12.sp,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // 地点信息
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "出发地点：${order.startLocation}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "到达地点：${order.endLocation}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 底部操作栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 删除按钮
            Row(
                modifier = Modifier
                    .clickable { }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "删除",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 重新预约按钮
            Button(
                onClick = { },
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "重新预约",
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Activity用于在Android中启动这个Screen
class CancelledOrdersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CancelledOrdersScreen(
                onNavigateBack = {
                    finish()
                },
                onOrderClick = { order ->
                    // 处理取消订单点击事件
                }
            )
        }
    }
}