package com.example.sellauto.clients.sellauto.payloads.auth

data class ResponseLoginPayload(
    val accessToken: String,
    val refreshToken: String,
)
