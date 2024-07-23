package com.yanfiq.streamfusion.data.retrofit.spotify

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object SpotifyApi {

    private var accessToken: String? = null

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            accessToken?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: SpotifyApiService = retrofit.create(SpotifyApiService::class.java)

    fun getAccessToken(): String? {
        return accessToken
    }

    fun fetchAccessToken(context: Context, clientId: String, clientSecret: String, callback: (Boolean) -> Unit) {
        val authString = "$clientId:$clientSecret"
        val encodedAuthString = Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)

        val requestBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(requestBody)
            .addHeader("Authorization", "Basic $encodedAuthString")
            .build()

        OkHttpClient().newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("SpotifyApi", "Failed to fetch access token: ${response.message}")
                        callback(false)
                        return
                    }
                    val responseBody = response.body?.string()
                    Log.d("SpotifyApi", "Access Token Response: $responseBody")
                    val accessTokenResponse = Gson().fromJson(responseBody, SpotifyAuthResponse::class.java)
                    accessToken = accessTokenResponse.access_token
                    callback(true)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("SpotifyApi", "Error fetching access token", e)
                callback(false)
            }
        })
    }
}
