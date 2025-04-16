package com.example.sellauto.clients.sellauto

import com.example.sellauto.clients.sellauto.payloads.ads.AdDetailsPayload
import com.example.sellauto.clients.sellauto.payloads.ads.AdListPayload
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming


const val ADS_URL: String = "/api/v1/ads"

interface SellAutoRestClient {
    @GET(ADS_URL)
    fun getAds(): Call<AdListPayload>

    @Streaming
    @GET("$ADS_URL/getPhoto/{photoId}")
    suspend fun getPhoto(@Path("photoId") photoId: Long) : Response<ResponseBody>

    @GET("$ADS_URL/{adId}")
    suspend fun getAd(@Path("adId") adId: Long): AdDetailsPayload
}