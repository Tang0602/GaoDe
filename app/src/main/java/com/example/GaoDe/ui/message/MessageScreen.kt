package com.example.GaoDe.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 数据结构定义
data class MessageInfo(
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val title: String,
    val subtitle: String,
    val timestamp: String
)

// 列表项类型
sealed class MessageListItem {
    data class MessageItem(val messageInfo: MessageInfo) : MessageListItem()
    data class DateSeparator(val text: String) : MessageListItem()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen() {
    // 根据截图精确复刻的消息数据
    val messageItems = listOf(
        MessageListItem.MessageItem(
            MessageInfo(
                icon = Icons.Default.Comment,
                iconBackgroundColor = Color(0xFFFFA726),
                title = "地点评论",
                subtitle = "恭喜你被活动君pick了！",
                timestamp = "2025/10/07"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                icon = Icons.Default.NotificationImportant,
                iconBackgroundColor = Color(0xFFEF5350),
                title = "通知权限提醒",
                subtitle = "提醒：1个重要权限待开启",
                timestamp = "2025/10/07"
            )
        ),
        MessageListItem.DateSeparator("两周前和长期未读消息"),
        MessageListItem.MessageItem(
            MessageInfo(
                icon = Icons.Default.Assignment,
                iconBackgroundColor = Color(0xFFFFA726),
                title = "订单提醒",
                subtitle = "打车·支付成功",
                timestamp = "2025/10/01"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                icon = Icons.Default.Person,
                iconBackgroundColor = Color(0xFF42A5F5),
                title = "打车会话",
                subtitle = "司机即将到达提醒: 司机将在2分钟内到达，请...",
                timestamp = "2025/10/01"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                icon = Icons.Default.DirectionsBus,
                iconBackgroundColor = Color(0xFF66BB6A),
                title = "公共交通动态",
                subtitle = "叮～请查收公交智能提醒！",
                timestamp = "2025/10/01"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                icon = Icons.Default.CardGiftcard,
                iconBackgroundColor = Color(0xFFFF7043),
                title = "天天领福利",
                subtitle = "十一去香港记得领一份免费蛋挞哦",
                timestamp = "2025/09/17"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                icon = Icons.Default.DirectionsRun,
                iconBackgroundColor = Color(0xFF66BB6A),
                title = "高德运动",
                subtitle = "叮～您有新的导航轨迹生成啦！",
                timestamp = "2025/08/28"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                icon = Icons.Default.Report,
                iconBackgroundColor = Color(0xFFEF5350),
                title = "高德快报",
                subtitle = "行程临时有变怎么办？五一出行必看！",
                timestamp = "2025/04/25"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                icon = Icons.Default.Hotel,
                iconBackgroundColor = Color(0xFF42A5F5),
                title = "高德酒店",
                subtitle = "您的酒店订单已确认",
                timestamp = "2025/04/20"
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "消息",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Archive,
                            contentDescription = "归档",
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "添加",
                            tint = Color.Gray
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            contentPadding = PaddingValues(0.dp)
        ) {
            items(messageItems) { item ->
                when (item) {
                    is MessageListItem.MessageItem -> {
                        MessageRow(message = item.messageInfo)
                    }
                    is MessageListItem.DateSeparator -> {
                        DateSeparator(text = item.text)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageRow(message: MessageInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧图标
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(message.iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = message.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 中间文本
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = message.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = message.subtitle,
                fontSize = 14.sp,
                color = Color(0xFF757575),
                maxLines = 1
            )
        }
        
        // 右侧时间戳
        Text(
            text = message.timestamp,
            fontSize = 12.sp,
            color = Color(0xFFBDBDBD)
        )
    }
}

@Composable
fun DateSeparator(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}