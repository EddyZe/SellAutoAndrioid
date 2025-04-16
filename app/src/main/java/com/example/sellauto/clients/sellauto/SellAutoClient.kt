package com.example.sellauto.clients.sellauto


import com.example.sellauto.clients.sellauto.payloads.ads.AdDetailsPayload
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

const val BASE_URL: String = "http://10.0.2.2:8082/"

class SellAutoClient {

    private var instance: SellAutoRestClient = getInstance()

    suspend fun getAds(): List<AdDetailsPayload> {
        val response = instance.getAds().execute()

        if (response.isSuccessful) {
            if (response.body() != null && response.body()?.ads != null) {
                return response.body()!!.ads
            }
        }

        return emptyList()
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
}