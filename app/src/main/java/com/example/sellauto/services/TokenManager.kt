package com.example.sellauto.services

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "sell_auto",
        Context.MODE_PRIVATE
    )

    fun saveTokens(access: String, refresh: String) {
        prefs.edit() {
            putString("ACCESS_TOKEN", access)
            putString("REFRESH_TOKEN", refresh)
            apply()
        }
    }

    fun getAccessToken(): String? =
        prefs.getString("ACCESS_TOKEN", null)

    fun getRefreshToken(): String? =
        prefs.getString("REFRESH_TOKEN", null)

    fun clearTokens() {
        prefs.edit().apply {
            remove("ACCESS_TOKEN")
            remove("REFRESH_TOKEN")
            apply()
        }
    }

}