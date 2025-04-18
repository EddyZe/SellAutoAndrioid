package com.example.sellauto.clients.sellauto.payloads.profile

import com.example.sellauto.enums.Role

data class AccountPayload(
    val accountId: Long,
    val email: String,
    val phoneNumber: String,
    val blocked: Boolean,
    val role: Role
)
