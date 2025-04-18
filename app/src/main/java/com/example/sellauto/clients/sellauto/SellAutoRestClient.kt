package com.example.sellauto.clients.sellauto

import com.example.sellauto.clients.sellauto.payloads.ads.AdDetailsPayload
import com.example.sellauto.clients.sellauto.payloads.ads.AdListPayload
import com.example.sellauto.clients.sellauto.payloads.auth.RequestLoginPayload
import com.example.sellauto.clients.sellauto.payloads.auth.ResponseLoginPayload
import com.example.sellauto.clients.sellauto.payloads.profile.ProfileDetailsPayload
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming


const val ADS_URL: String = "/api/v1/ads"
const val AUTH_URL: String = "/api/v1/auth"
const val PROFILE_URL: String = "/api/v1/profiles"

interface SellAutoRestClient {
    @GET(ADS_URL)
    fun getAds(): Call<AdListPayload>

    @GET("$ADS_URL/user/{userId}")
    suspend fun getMyAds(@Path("userId") userId: Long): Response<AdListPayload>

    @DELETE("$ADS_URL/{adId}")
    suspend fun deleteAd(@Header("Authorization") token: String, @Path("adId") adId: Long): Response<ResponseBody>

    @Streaming
    @GET("$ADS_URL/getPhoto/{photoId}")
    suspend fun getPhoto(@Path("photoId") photoId: Long): Response<ResponseBody>

    @GET("$ADS_URL/{adId}")
    suspend fun getAd(@Path("adId") adId: Long): AdDetailsPayload

    @POST("$AUTH_URL/login")
    suspend fun login(@Body data: RequestLoginPayload): Response<ResponseLoginPayload>

    @GET("$PROFILE_URL/my")
    suspend fun getCurrentProfile(@Header("Authorization") token: String): Response<ProfileDetailsPayload>

    @POST("$AUTH_URL/refresh")
    suspend fun refreshToken(@Header("Authorization") refreshToken: String): Response<ResponseLoginPayload>

    @GET("/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<ResponseBody>
}