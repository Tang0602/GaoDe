package com.example.amap_sim.data.repository

import com.example.amap_sim.data.datasource.AssetDataSource
import com.example.amap_sim.data.datasource.LocalStorageDataSource
import com.example.amap_sim.model.Message
import com.google.gson.reflect.TypeToken

class MessageRepository(
    private val assetDataSource: AssetDataSource,
    private val localStorageDataSource: LocalStorageDataSource
) {
    private val sharedMessagesFileName = "shared_messages.json"

    // 分享消息数据类
    data class SharedMessage(
        val id: String,
        val contactId: String,
        val contactName: String,
        val message: String,
        val timestamp: Long
    )

    fun getMessages(): List<Message> {
        return assetDataSource.loadFromAssets("data/messages.json", object : TypeToken<List<Message>>() {})
    }

    fun getSharedMessages(): List<SharedMessage> {
        // 获取默认聊天记录（从messages.json加载）
        val defaultMessages = getDefaultChatMessages()

        // 获取用户自己发送的消息
        val userMessages = localStorageDataSource.readFromFile(
            sharedMessagesFileName,
            object : TypeToken<List<SharedMessage>>() {}
        ) ?: emptyList()

        // 合并默认消息和用户消息，按时间排序
        return (defaultMessages + userMessages).sortedBy { it.timestamp }
    }

    private fun getDefaultChatMessages(): List<SharedMessage> {
        // 从messages.json中加载与爸妈的聊天记录
        val allMessages = getMessages()
        val parentMessages = allMessages.filter { message ->
            message.senderId == "dad" || message.senderId == "mom" ||
            message.receiverId == "dad" || message.receiverId == "mom"
        }.map { message ->
            val contactId = when {
                message.senderId == "dad" || message.receiverId == "dad" -> "dad"
                message.senderId == "mom" || message.receiverId == "mom" -> "mom"
                else -> "unknown"
            }
            val contactName = when (contactId) {
                "dad" -> "爸爸"
                "mom" -> "妈妈"
                else -> "未知联系人"
            }

            SharedMessage(
                id = message.id,
                contactId = contactId,
                contactName = contactName,
                message = message.content,
                timestamp = message.timestamp
            )
        }

        return parentMessages.ifEmpty {
            // 如果没有找到爸妈的消息，使用默认数据
            val currentTime = System.currentTimeMillis()
            listOf(
                SharedMessage(
                    id = "default_dad_1",
                    contactId = "dad",
                    contactName = "爸爸",
                    message = "儿子，今天天气挺好的，记得多喝水",
                    timestamp = currentTime - 3600000 * 24
                ),
                SharedMessage(
                    id = "default_mom_1",
                    contactId = "mom",
                    contactName = "妈妈",
                    message = "宝贝，记得按时吃饭",
                    timestamp = currentTime - 3600000 * 12
                )
            )
        }
    }

    fun addSharedMessage(contactId: String, contactName: String, message: String): Boolean {
        val existingMessages = localStorageDataSource.readFromFile(
            sharedMessagesFileName,
            object : TypeToken<List<SharedMessage>>() {}
        ) ?: emptyList()

        val newMessage = SharedMessage(
            id = "shared_${System.currentTimeMillis()}",
            contactId = contactId,
            contactName = contactName,
            message = message,
            timestamp = System.currentTimeMillis()
        )

        // 只保存用户新增的消息到文件
        val userMessages = existingMessages.toMutableList()
        userMessages.add(0, newMessage)

        // 使用格式化的JSON保存
        localStorageDataSource.writeToFile(sharedMessagesFileName, userMessages, pretty = true)
        return true
    }

    fun getMessagesForContact(contactId: String): List<SharedMessage> {
        return getSharedMessages().filter { it.contactId == contactId }
    }
}
