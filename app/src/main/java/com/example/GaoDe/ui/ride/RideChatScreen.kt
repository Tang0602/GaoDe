package com.example.GaoDe.ui.ride

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun RideChatScreen(
    sessionId: String = "ride_session_001",
    onNavigateBack: () -> Unit = {},
    onCallDriver: () -> Unit = {},
    onRateDriver: () -> Unit = {}
) {
    // 模拟会话数据
    val session = remember {
        RideSession(
            sessionId = sessionId,
            driverName = "田师傅",
            driverAvatar = null,
            orderType = "实时单",
            orderId = "GDC20251001001",
            estimatedPrice = "¥10.91",
            status = RideStatus.TRIP_COMPLETED,
            createdAt = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
            lastMessageSummary = "司机即将到达提醒: 司机将在 2分钟内到达，请您做好准备。",
            messages = listOf(
                ChatMessage(
                    id = "msg_001",
                    type = ChatMessageType.ORDER_STATUS,
                    content = "订单已创建: 订单号 GDC20251001001，费用预估 ¥10.91。",
                    senderName = "系统",
                    timestamp = System.currentTimeMillis() - 30 * 60 * 1000,
                    orderId = "GDC20251001001",
                    amount = "¥10.91"
                ),
                ChatMessage(
                    id = "msg_002",
                    type = ChatMessageType.SYSTEM_NOTIFICATION,
                    content = "司机即将到达提醒: 司机将在 2分钟内到达，请您做好准备。",
                    senderName = "系统",
                    timestamp = System.currentTimeMillis() - 25 * 60 * 1000
                ),
                ChatMessage(
                    id = "msg_003",
                    type = ChatMessageType.DRIVER_MESSAGE,
                    content = "好的，乘客您好，我已经到达您上车点，在 D口等您。",
                    senderName = "田师傅",
                    timestamp = System.currentTimeMillis() - 20 * 60 * 1000
                ),
                ChatMessage(
                    id = "msg_004",
                    type = ChatMessageType.PASSENGER_MESSAGE,
                    content = "好的，我马上出来，大概 1分钟。",
                    senderName = "乘客",
                    timestamp = System.currentTimeMillis() - 18 * 60 * 1000
                ),
                ChatMessage(
                    id = "msg_005",
                    type = ChatMessageType.SYSTEM_NOTIFICATION,
                    content = "行程结束: 您已支付 ¥10.91，感谢使用高德打车。",
                    senderName = "系统",
                    timestamp = System.currentTimeMillis() - 10 * 60 * 1000,
                    amount = "¥10.91"
                )
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
                    text = "打车会话",
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
        
        // 顶部会话摘要区
        ChatHeaderSection(session = session)
        
        // 消息列表
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            state = rememberLazyListState(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(session.messages) { message ->
                ChatMessageItem(message = message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ChatHeaderSection(session: RideSession) {
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(session.createdAt))
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 司机信息
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 司机头像
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "司机头像",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // 司机姓名和订单类型
                Column {
                    Text(
                        text = session.driverName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // 订单类型标签
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Text(
                            text = session.orderType,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 日期
            Text(
                text = formattedDate,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 消息摘要
        Text(
            text = session.lastMessageSummary,
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }
    val formattedTime = dateFormat.format(Date(message.timestamp))
    
    when (message.type) {
        ChatMessageType.SYSTEM_NOTIFICATION, ChatMessageType.ORDER_STATUS -> {
            SystemMessageCard(message = message, formattedTime = formattedTime)
        }
        ChatMessageType.DRIVER_MESSAGE -> {
            DriverMessageCard(message = message, formattedTime = formattedTime)
        }
        ChatMessageType.PASSENGER_MESSAGE -> {
            PassengerMessageCard(message = message, formattedTime = formattedTime)
        }
    }
}

@Composable
fun SystemMessageCard(message: ChatMessage, formattedTime: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 时间戳
        Text(
            text = formattedTime,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // 系统消息卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // 订单信息（如果有）
                message.orderId?.let { orderId ->
                    Text(
                        text = "订单号: $orderId",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                Text(
                    text = message.content,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 20.sp
                )
                
                // 金额信息（如果有）
                message.amount?.let { amount ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = amount,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                }
            }
        }
    }
}

@Composable
fun DriverMessageCard(message: ChatMessage, formattedTime: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 时间戳
        Text(
            text = formattedTime,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // 司机头像
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "司机头像",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 消息内容
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 司机姓名
                Text(
                    text = message.senderName ?: "司机",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                // 消息气泡
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFE3F2FD),
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = message.content,
                        fontSize = 14.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PassengerMessageCard(message: ChatMessage, formattedTime: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 时间戳
        Text(
            text = formattedTime,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .align(Alignment.End)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            // 消息内容
            Column(
                modifier = Modifier.widthIn(max = 280.dp),
                horizontalAlignment = Alignment.End
            ) {
                // 消息气泡
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFE8F5E8),
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = message.content,
                        fontSize = 14.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 乘客头像
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "乘客头像",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// 示例Activity，用于在Android中启动这个Screen
class RideChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sessionId = intent.getStringExtra("SESSION_ID") ?: "ride_session_001"
        
        setContent {
            RideChatScreen(
                sessionId = sessionId,
                onNavigateBack = {
                    finish()
                },
                onCallDriver = {
                    // 处理呼叫司机事件
                },
                onRateDriver = {
                    // 处理评价司机事件
                }
            )
        }
    }
}