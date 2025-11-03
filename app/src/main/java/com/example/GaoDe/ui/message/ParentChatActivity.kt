package com.example.GaoDe.ui.message

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import androidx.compose.ui.text.style.TextAlign
import com.example.GaoDe.data.DataManager
import com.example.GaoDe.ui.theme.GaoDeTheme
import java.text.SimpleDateFormat
import java.util.*

class ParentChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val contactId = intent.getStringExtra("CONTACT_ID") ?: "dad"
        val contactName = intent.getStringExtra("CONTACT_NAME") ?: "爸爸"
        val avatarFileName = intent.getStringExtra("AVATAR_FILE") ?: "friend_1.jpg"
        
        setContent {
            GaoDeTheme {
                ParentChatScreen(
                    contactId = contactId,
                    contactName = contactName,
                    avatarFileName = avatarFileName,
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentChatScreen(
    contactId: String,
    contactName: String,
    avatarFileName: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val dataManager = remember { DataManager(context) }
    var messageText by remember { mutableStateOf("") }
    var chatMessages by remember { mutableStateOf(emptyList<ChatMessage>()) }
    
    // Load existing messages and convert to chat format
    LaunchedEffect(contactId) {
        val sharedMessages = dataManager.getMessagesForContact(contactId)
        chatMessages = sharedMessages.map { sharedMsg ->
            ChatMessage(
                id = sharedMsg.id,
                content = sharedMsg.message,
                timestamp = sharedMsg.timestamp,
                isFromUser = true,
                messageType = if (sharedMsg.message.contains("位置:") || sharedMsg.message.contains("地点:")) 
                    ChatMessageType.LOCATION else ChatMessageType.TEXT
            )
        }
    }
    
    // Load contact avatar
    val contactBitmap = remember(avatarFileName) {
        try {
            val inputStream = context.assets.open("avatar/$avatarFileName")
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Contact avatar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF5F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (contactBitmap != null) {
                                Image(
                                    bitmap = contactBitmap.asImageBitmap(),
                                    contentDescription = contactName,
                                    modifier = Modifier.size(40.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = contactName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            // Message input area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Location sharing button
                        IconButton(
                            onClick = {
                                // Share a sample location
                                val locationMessage = ChatMessage(
                                    id = "msg_${System.currentTimeMillis()}",
                                    content = "位置: 武汉市洪山区华中科技大学 - 我在这里",
                                    timestamp = System.currentTimeMillis(),
                                    isFromUser = true,
                                    messageType = ChatMessageType.LOCATION
                                )
                                chatMessages = chatMessages + locationMessage
                                
                                // Save location message to DataManager
                                dataManager.addSharedMessage(contactId, contactName, "您分享了地点: 武汉市洪山区华中科技大学 - 我在这里")
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "分享位置",
                                tint = Color(0xFF42A5F5)
                            )
                        }
                        
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("输入消息...") },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF42A5F5),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                // Add new message to chat
                                val newMessage = ChatMessage(
                                    id = "msg_${System.currentTimeMillis()}",
                                    content = messageText,
                                    timestamp = System.currentTimeMillis(),
                                    isFromUser = true,
                                    messageType = ChatMessageType.TEXT
                                )
                                chatMessages = chatMessages + newMessage
                                
                                // Save message to DataManager
                                dataManager.addSharedMessage(contactId, contactName, messageText)
                                
                                messageText = ""
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF42A5F5)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "发送",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            items(chatMessages) { message ->
                ChatMessageItem(message = message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

data class ChatMessage(
    val id: String,
    val content: String,
    val timestamp: Long,
    val isFromUser: Boolean,
    val messageType: ChatMessageType = ChatMessageType.TEXT
)

enum class ChatMessageType {
    TEXT,
    LOCATION
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (message.isFromUser) Color(0xFF42A5F5) else Color.White
    val textColor = if (message.isFromUser) Color.White else Color.Black
    
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
        ) {
            when (message.messageType) {
                ChatMessageType.LOCATION -> {
                    LocationMessageCard(
                        content = message.content,
                        isFromUser = message.isFromUser
                    )
                }
                ChatMessageType.TEXT -> {
                    Surface(
                        modifier = Modifier.widthIn(max = 280.dp),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                            bottomEnd = if (message.isFromUser) 4.dp else 16.dp
                        ),
                        color = backgroundColor,
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = message.content,
                            modifier = Modifier.padding(12.dp),
                            color = textColor,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LocationMessageCard(
    content: String,
    isFromUser: Boolean
) {
    val alignment = if (isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "位置分享",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = content.removePrefix("您分享了地点: ").removePrefix("位置: "),
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🗺️ 地图预览",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}