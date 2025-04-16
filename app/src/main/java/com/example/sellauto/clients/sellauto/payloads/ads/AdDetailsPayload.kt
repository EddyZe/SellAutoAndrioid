package com.example.sellauto.clients.sellauto.payloads.ads

import java.util.Date

data class AdDetailsPayload(
    val adId: Long,
    val title: String,
    val description: String,
    val prices: List<PriceBasePayload>,
    val isActive: Boolean,
    val createAt: Date,
    val user: UserBasePayload,
    val car: CarDetailsDto,
)