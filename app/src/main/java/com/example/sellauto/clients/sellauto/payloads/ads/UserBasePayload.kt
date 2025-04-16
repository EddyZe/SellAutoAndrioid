package com.example.sellauto.clients.sellauto.payloads.ads

data class UserBasePayload(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val rating: Double,
)
