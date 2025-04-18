package com.example.sellauto.clients.sellauto.payloads.profile

import com.example.sellauto.clients.sellauto.payloads.ads.PriceBasePayload
import java.util.Date

data class AdProfilePayload(

    val adId: Long,
    val title: String,
    val description: String,
    val prices: List<PriceBasePayload>,
    val isActive: Boolean,
    val createdAt: Date,
    val car: CarProfilePayload,
    val chats: List<ChatBasePayload>,
)
