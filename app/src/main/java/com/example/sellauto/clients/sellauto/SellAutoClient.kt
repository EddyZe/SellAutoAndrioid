package com.example.sellauto.clients.sellauto


import android.annotation.SuppressLint
import android.content.Context
import com.example.sellauto.clients.sellauto.payloads.ads.AdDetailsPayload
import com.example.sellauto.clients.sellauto.payloads.auth.RequestLoginPayload
import com.example.sellauto.clients.sellauto.payloads.auth.ResponseLoginPayload
import com.example.sellauto.clients.sellauto.payloads.profile.ProfileDetailsPayload
import com.example.sellauto.exception.SellAutoApiException
import com.example.sellauto.exception.UnAuthException
import com.example.sellauto.services.TokenManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory.create
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


const val BASE_URL: String = "http://10.0.2.2:8082/"

class SellAutoClient(
    context: Context
) {


    private var instance: SellAutoRestClient = getInstance()
    private var tokenManager: TokenManager = TokenManager(context)


    suspend fun getAds(): List<AdDetailsPayload> {
        val response = instance.getAds().execute()

        if (response.isSuccessful) {
            if (response.body() != null && response.body()?.ads != null) {
                return response.body()!!.ads
            }
        }

        return emptyList()
    }

    @SuppressLint("NewApi")
    suspend fun getMyAds(): List<AdDetailsPayload> {
        val profile = getMyProfile()
        val response = instance.getMyAds(profile.userId)

        if (response.isSuccessful) {
            if (response.body() != null && response.body()?.ads != null) {
                return response.body()!!.ads
            }
        } else if (response.code() == 401) {
            refreshTokens()
            getMyAds()
        }
        return emptyList()
    }

    suspend fun getMyProfile(): ProfileDetailsPayload {
        val currentToken = getCurrentToken();
        return instance.getCurrentProfile("Bearer $currentToken").let {
            if (it.isSuccessful) {
                return it.body()!!
            } else if (it.code() == 401) {
                refreshTokens()
                return getMyProfile()
            } else {
                throw SellAutoApiException(
                    badResponse(it)["error"]?.toString()
                        ?: "Неизвестная ошибка"
                )
            }
        }
    }

    suspend fun deleteAd(adId: Long) {
        val currentToken = getCurrentToken()
        val resp = instance.deleteAd("Bearer $currentToken", adId)

        if (resp.code() == 401) {
            refreshTokens()
            deleteAd(adId)
            return
        }

        if (!resp.isSuccessful) {
            throw SellAutoApiException(badResponse(resp)["error"].toString())
        }
    }

    private fun badResponse(it: Response<*>): Map<String, Any> {
        return try {
            val errorJson = it.errorBody()?.use { body ->
                body.string()
            } ?: "\"error\": \"Неизвестная ошибка\""

            val type = object : TypeToken<Map<String, Any>>() {}.type
            Gson().fromJson<Map<String, Any>>(errorJson, type)
        } catch (e: IOException) {
            mapOf("error" to "Ошибка ввода-вывода")
        } catch (e: JsonSyntaxException) {
            mapOf("error" to "Некорректный формат ответа")
        } catch (e: Exception) {
            mapOf("error" to "Неизвестная ошибка: ${e.javaClass.simpleName}")
        }
    }

    suspend fun login(email: String, password: String): ResponseLoginPayload {
        val data = RequestLoginPayload(email, password)

        return instance.login(data).let {
            if (it.isSuccessful) {
                val body = it.body()!!
                tokenManager.clearTokens()
                tokenManager.saveTokens(body.accessToken, body.refreshToken)
                return body
            } else {
                throw UnAuthException("Не верный email или пароль")
            }
        }
    }

    suspend fun logout() {
        val currentToken = getCurrentToken()
        val resp = instance.logout("Bearer $currentToken")

        if (resp.isSuccessful) {
            tokenManager.clearTokens()
        } else if (resp.code() == 401) {
            tokenManager.clearTokens()
        } else {
            throw SellAutoApiException(badResponse(resp)["error"].toString())
        }
    }


    suspend fun getPhoto(photoId: Long) = instance.getPhoto(photoId)

    suspend fun getAd(adId: Long) = instance.getAd(adId)


    private fun getInstance(): SellAutoRestClient =
        buildRetrofit().create(SellAutoRestClient::class.java)


    private fun buildRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            create(
                GsonBuilder()
                    .registerTypeAdapter(Date::class.java, createCustomGsonDeserializer())
                    .create()
            )
        )
        .build()

    private fun createCustomGsonDeserializer(): JsonDeserializer<Date> =
        JsonDeserializer { json, _, _ ->
            val dateStr = json.asString
            val dateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            try {
                dateFormat.parse(dateStr)
            } catch (e: Exception) {
                throw JsonParseException("Не удалось разобрать дату: $dateStr", e)
            }
        }

    private suspend fun getCurrentToken(): String {
        var currentToken = tokenManager.getAccessToken()
        if (currentToken == null) {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken == null) {
                throw UnAuthException("Вы не авторизованы")
            }
            refreshTokens()
            return getCurrentToken()
        }

        return currentToken
    }

    private suspend fun refreshTokens() {
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken == null) {
            throw UnAuthException("Вы не авторизованы")
        }
        var resp = instance.refreshToken("Bearer $refreshToken")
        if (resp.isSuccessful) {
            tokenManager.saveTokens(resp.body()!!.accessToken, resp.body()!!.refreshToken)
        } else {
            throw UnAuthException("Вы не авторизованы")
        }
    }
}