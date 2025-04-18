package com.example.sellauto.clients.sellauto.payloads.profile

data class ChatBasePayload(
    val chatId: Long,
    val messages: List<MessageBasePayload>,
)
