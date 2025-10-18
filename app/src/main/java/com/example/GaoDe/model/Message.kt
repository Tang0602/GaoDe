package com.example.GaoDe.model

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Long,
    val messageType: MessageType = MessageType.TEXT,
    val isRead: Boolean = false,
    val attachmentUrl: String? = null
)

enum class MessageType {
    TEXT,
    IMAGE,
    LOCATION,
    AUDIO
}

data class ChatConversation(
    val id: String,
    val participantIds: List<String>,
    val messages: List<Message> = emptyList(),
    val lastMessage: Message? = null,
    val lastActivity: Long,
    val isGroup: Boolean = false,
    val groupName: String? = null
)