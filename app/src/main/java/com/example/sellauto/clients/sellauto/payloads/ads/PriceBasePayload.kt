package com.example.sellauto.clients.sellauto.payloads.ads

import java.util.Date

data class PriceBasePayload(
    val priceId: Int,
    val price: Double,
    val createdAt: Date,
)
