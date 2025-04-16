package com.example.sellauto

import com.example.sellauto.clients.sellauto.SellAutoClient
import com.example.sellauto.clients.sellauto.SellAutoRestClient
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val customDateDeserializer = JsonDeserializer { json, _, _ ->
            val dateStr = json.asString
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            try {
                dateFormat.parse(dateStr)
            } catch (e: Exception) {
                throw JsonParseException("Не удалось разобрать дату: $dateStr", e)
            }
        }

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:8082/")
            .addConverterFactory(
                create(
                    GsonBuilder()
                        .registerTypeAdapter(Date::class.java, customDateDeserializer)
                        .create()
                )
            )
            .build()

        val clientSellAuto = retrofit.create(SellAutoRestClient::class.java)

        val ads = clientSellAuto.getAds().execute().body()

        ads?.ads?.forEach {
            println(it.title)
        }

        assertTrue(ads?.ads != null)
    }

}