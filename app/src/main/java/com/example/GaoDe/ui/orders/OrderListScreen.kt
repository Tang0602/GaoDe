package com.example.GaoDe.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun OrderListScreen(
    onNavigateBack: () -> Unit = {},
    onOrderClick: (Order) -> Unit = {}
) {
    var selectedStatus by remember { mutableStateOf(OrderStatusTab.ALL) }
    var selectedOrderType: OrderType? by remember { mutableStateOf(null) }
    
    // 模拟订单数据
    val sampleOrders = remember {
        listOf(
            Order(
                id = "order_001",
                orderType = OrderType.TAXI,
                orderTitle = "及时特选经济型",
                status = OrderStatus.COMPLETED,
                price = 10.91,
                createdAt = System.currentTimeMillis() - 2 * 60 * 60 * 1000, // 2小时前
                startLocation = "虎泉地铁站 D口",
                endLocation = "华中师范大学 (南湖校区)",
                isRealTime = true
            ),
            Order(
                id = "order_002",
                orderType = OrderType.TAXI,
                orderTitle = "风韵特选经济型",
                status = OrderStatus.COMPLETED,
                price = 15.66,
                createdAt = System.currentTimeMillis() - 5 * 60 * 60 * 1000, // 5小时前
                startLocation = "光谷广场地铁站 A出口",
                endLocation = "武汉大学 (文理学部)",
                isRealTime = true
            ),
            Order(
                id = "order_003",
                orderType = OrderType.TAXI,
                orderTitle = "聚的出租车",
                status = OrderStatus.COMPLETED,
                price = 23.45,
                createdAt = System.currentTimeMillis() - 8 * 60 * 60 * 1000, // 8小时前
                startLocation = "武汉火车站",
                endLocation = "海底捞火锅 (泛悦·城市奥特莱斯店)",
                isRealTime = false
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
                    text = "我的订单",
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
        
        // 订单状态筛选栏
        OrderStatusTabRow(
            selectedTab = selectedStatus,
            onTabSelected = { selectedStatus = it }
        )
        
        // 订单类型筛选栏
        OrderTypeFilterRow(
            selectedType = selectedOrderType,
            onTypeSelected = { selectedOrderType = it }
        )
        
        // 订单列表
        val filteredOrders = sampleOrders.filter { order ->
            val statusMatch = when (selectedStatus) {
                OrderStatusTab.ALL -> true
                OrderStatusTab.COMPLETED -> order.status == OrderStatus.COMPLETED
                OrderStatusTab.PENDING -> order.status == OrderStatus.PENDING
                OrderStatusTab.CANCELLED -> order.status == OrderStatus.CANCELLED
            }
            val typeMatch = selectedOrderType == null || order.orderType == selectedOrderType
            statusMatch && typeMatch
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = rememberLazyListState(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredOrders) { order ->
                OrderCard(
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

enum class OrderStatusTab {
    ALL,
    COMPLETED,
    PENDING,
    CANCELLED
}

@Composable
fun OrderStatusTabRow(
    selectedTab: OrderStatusTab,
    onTabSelected: (OrderStatusTab) -> Unit
) {
    TabRow(
        selectedTabIndex = 0,
        containerColor = Color.White,
        contentColor = Color.Blue,
        modifier = Modifier.fillMaxWidth()
    ) {
        Tab(
            selected = selectedTab == OrderStatusTab.ALL,
            onClick = { onTabSelected(OrderStatusTab.ALL) },
            text = {
                Text(
                    text = "全部",
                    fontWeight = if (selectedTab == OrderStatusTab.ALL) FontWeight.Bold else FontWeight.Normal
                )
            }
        )
        Tab(
            selected = selectedTab == OrderStatusTab.COMPLETED,
            onClick = { onTabSelected(OrderStatusTab.COMPLETED) },
            text = {
                Text(
                    text = "已完成",
                    fontWeight = if (selectedTab == OrderStatusTab.COMPLETED) FontWeight.Bold else FontWeight.Normal
                )
            }
        )
        Tab(
            selected = selectedTab == OrderStatusTab.PENDING,
            onClick = { onTabSelected(OrderStatusTab.PENDING) },
            text = {
                Text(
                    text = "进行中",
                    fontWeight = if (selectedTab == OrderStatusTab.PENDING) FontWeight.Bold else FontWeight.Normal
                )
            }
        )
        Tab(
            selected = selectedTab == OrderStatusTab.CANCELLED,
            onClick = { onTabSelected(OrderStatusTab.CANCELLED) },
            text = {
                Text(
                    text = "已取消",
                    fontWeight = if (selectedTab == OrderStatusTab.CANCELLED) FontWeight.Bold else FontWeight.Normal
                )
            }
        )
    }
}

@Composable
fun OrderTypeFilterRow(
    selectedType: OrderType?,
    onTypeSelected: (OrderType?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 全部
        FilterChip(
            selected = selectedType == null,
            onClick = { onTypeSelected(null) },
            label = {
                Text(
                    text = "全部",
                    fontSize = 14.sp,
                    fontWeight = if (selectedType == null) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFF2196F3),
                selectedLabelColor = Color.White
            )
        )
        
        // 各个订单类型
        OrderType.values().forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = {
                    Text(
                        text = type.displayName,
                        fontSize = 14.sp,
                        fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2196F3),
                    selectedLabelColor = Color.White
                )
            )
        }
        
        // 更多指示器
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = "更多类型",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun OrderCard(
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
            Text(
                text = order.status.displayName,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 价格
            Text(
                text = "¥${String.format("%.2f", order.price)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 时间
        Text(
            text = "下单时间：$formattedTime",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
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
            
            // 开发票按钮
            OutlinedButton(
                onClick = { },
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color.Gray)
                )
            ) {
                Text(
                    text = "开发票",
                    fontSize = 12.sp
                )
            }
            
            // 呼叫返程按钮
            OutlinedButton(
                onClick = { },
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color.Gray)
                )
            ) {
                Text(
                    text = "呼叫返程",
                    fontSize = 12.sp
                )
            }
            
            // 再来一单按钮
            Button(
                onClick = { },
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "再来一单",
                    fontSize = 12.sp
                )
            }
        }
    }
}

// 示例Activity，用于在Android中启动这个Screen
class OrderListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrderListScreen(
                onNavigateBack = {
                    finish()
                },
                onOrderClick = { order ->
                    // 处理订单点击事件
                }
            )
        }
    }
}