package com.example.GaoDe.ui.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.graphics.BitmapFactory
import com.example.GaoDe.ui.ride.RideChatActivity

// 数据结构定义
data class MessageInfo(
    val id: String,
    val icon: ImageVector?,
    val iconBackgroundColor: Color,
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val avatarFileName: String? = null
)

// 联系人数据
data class Contact(
    val id: String,
    val name: String,
    val avatarFileName: String
)

// 列表项类型
sealed class MessageListItem {
    data class MessageItem(val messageInfo: MessageInfo) : MessageListItem()
    data class DateSeparator(val text: String) : MessageListItem()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen() {
    val context = LocalContext.current
    val dataManager = remember { com.example.GaoDe.data.DataManager(context) }
    
    // 使用LaunchedEffect来定期刷新消息
    LaunchedEffect(Unit) {
        // 示例1: 指令1 - 点击消息页面
        com.example.GaoDe.MainActivity.recordLog(
            context = context,
            event = com.example.GaoDe.LogEvent.MESSAGE_PAGE_NAVIGATE,
            action = "点击消息页面",
            page = "MessageScreen"
        )
    }
    
    // 打车会话点击处理
    val onRideChatClicked = { sessionId: String ->
        val intent = Intent(context, RideChatActivity::class.java).apply {
            putExtra("SESSION_ID", sessionId)
        }
        context.startActivity(intent)
    }
    // 联系人列表
    val contacts = listOf(
        Contact(id = "dad", name = "爸爸", avatarFileName = "friend_1.jpg"),
        Contact(id = "mom", name = "妈妈", avatarFileName = "friend_2.jpg")
    )
    
    // 动态生成联系人消息，包含最新分享的消息
    val dadMessages = dataManager.getMessagesForContact("dad")
    val momMessages = dataManager.getMessagesForContact("mom")
    
    val messageItems = listOf(
        MessageListItem.MessageItem(
            MessageInfo(
                id = "contact_dad",
                icon = null,
                iconBackgroundColor = Color.Transparent,
                title = "爸爸",
                subtitle = if (dadMessages.isNotEmpty()) "您分享了地点: ${dadMessages.first().message.substringBefore(" - ")}" else "点击查看聊天记录",
                timestamp = "2025/10/07",
                avatarFileName = "friend_1.jpg"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                id = "contact_mom",
                icon = null,
                iconBackgroundColor = Color.Transparent,
                title = "妈妈",
                subtitle = if (momMessages.isNotEmpty()) "您分享了地点: ${momMessages.first().message.substringBefore(" - ")}" else "点击查看聊天记录",
                timestamp = "2025/10/07",
                avatarFileName = "friend_2.jpg"
            )
        ),
        MessageListItem.DateSeparator("系统消息"),
        MessageListItem.MessageItem(
            MessageInfo(
                id = "msg_location_comment",
                icon = Icons.Default.Comment,
                iconBackgroundColor = Color(0xFFFFA726),
                title = "地点评论",
                subtitle = "恭喜你被活动君pick了！",
                timestamp = "2025/10/07"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                id = "msg_notification",
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
                id = "msg_order_reminder",
                icon = Icons.Default.Assignment,
                iconBackgroundColor = Color(0xFFFFA726),
                title = "订单提醒",
                subtitle = "打车·支付成功",
                timestamp = "2025/10/01"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                id = "ride_session_001",
                icon = Icons.Default.Person,
                iconBackgroundColor = Color(0xFF42A5F5),
                title = "打车会话",
                subtitle = "司机即将到达提醒: 司机将在2分钟内到达，请...",
                timestamp = "2025/10/01"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                id = "msg_bus_info",
                icon = Icons.Default.DirectionsBus,
                iconBackgroundColor = Color(0xFF66BB6A),
                title = "公共交通动态",
                subtitle = "叮～请查收公交智能提醒！",
                timestamp = "2025/10/01"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                id = "msg_benefits",
                icon = Icons.Default.CardGiftcard,
                iconBackgroundColor = Color(0xFFFF7043),
                title = "天天领福利",
                subtitle = "十一去香港记得领一份免费蛋挞哦",
                timestamp = "2025/09/17"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                id = "msg_sports",
                icon = Icons.Default.DirectionsRun,
                iconBackgroundColor = Color(0xFF66BB6A),
                title = "高德运动",
                subtitle = "叮～您有新的导航轨迹生成啦！",
                timestamp = "2025/08/28"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                id = "msg_news",
                icon = Icons.Default.Report,
                iconBackgroundColor = Color(0xFFEF5350),
                title = "高德快报",
                subtitle = "行程临时有变怎么办？五一出行必看！",
                timestamp = "2025/04/25"
            )
        ),
        MessageListItem.MessageItem(
            MessageInfo(
                id = "msg_hotel",
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
                        MessageRow(
                            message = item.messageInfo,
                            onRideChatClicked = onRideChatClicked
                        )
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
fun MessageRow(
    message: MessageInfo,
    onRideChatClicked: (String) -> Unit = {}
) {
    val context = LocalContext.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            // --- 修改后 ---
            .clickable {
                // 如果是打车会话消息，处理点击事件
                if (message.id.startsWith("ride_")) {
                    onRideChatClicked(message.id)
                } else if (message.id.startsWith("contact_")) {
                    // 处理联系人点击事件
                    val contactId = when (message.id) {
                        "contact_dad" -> "dad"
                        "contact_mom" -> "mom"
                        else -> return@clickable
                    }

                    // !!! 在这里根据 contactId 添加日志记录 !!!
                    if (contactId == "mom") {
                        // 指令 #6: 进入与妈妈的聊天界面
                        com.example.GaoDe.MainActivity.recordLog(
                            context = context,
                            event = com.example.GaoDe.LogEvent.MOM_CHAT, // 确保枚举名正确
                            action = "进入与妈妈的聊天界面",
                            page = "聊天页面"
                        )
                    }

                    // --- 保持原有的导航逻辑不变 ---
                    val contactName = when (contactId) {
                        "dad" -> "爸爸"
                        "mom" -> "妈妈"
                        else -> return@clickable
                    }
                    val avatarFile = when (contactId) {
                        "dad" -> "friend_1.jpg"
                        "mom" -> "friend_2.jpg"
                        else -> return@clickable
                    }

                    val intent = Intent(context, ParentChatActivity::class.java).apply {
                        putExtra("CONTACT_ID", contactId)
                        putExtra("CONTACT_NAME", contactName)
                        putExtra("AVATAR_FILE", avatarFile)
                    }
                    context.startActivity(intent)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧图标或头像
        if (message.avatarFileName != null) {
            // 显示联系人头像
            val bitmap = remember(message.avatarFileName) {
                try {
                    val inputStream = context.assets.open("avatar/${message.avatarFileName}")
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    null
                }
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = message.title,
                        modifier = Modifier.size(48.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        } else {
            // 显示系统消息图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(message.iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                message.icon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
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