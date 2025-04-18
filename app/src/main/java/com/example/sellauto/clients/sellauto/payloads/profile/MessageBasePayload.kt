package com.example.sellauto.clients.sellauto.payloads.profile

import java.util.Date

data class MessageBasePayload(
    val messageId: Long,
    val senderName: String,
    val message: String,
    val createdAt: Date,
)
