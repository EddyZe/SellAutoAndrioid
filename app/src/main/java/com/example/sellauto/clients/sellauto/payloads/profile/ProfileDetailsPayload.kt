package com.example.sellauto.clients.sellauto.payloads.profile

data class ProfileDetailsPayload(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val rating: Double,
    val account: AccountPayload,
    val ads: List<AdProfilePayload>,
)
