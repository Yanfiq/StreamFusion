package com.yanfiq.streamfusion.data.retrofit.spotify

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.yanfiq.streamfusion.BuildConfig
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object SpotifyApi {
    private const val PREFS_FILENAME = "encrypted_prefs"
    private const val TAG = "SpotifyApi"

    private var accessToken: String? = null

    private lateinit var sharedPreferences: EncryptedSharedPreferences

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

    fun initialize(context: Context) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPreferences = EncryptedSharedPreferences.create(
            PREFS_FILENAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    private fun ensureInitialized(context: Context) {
        if (!this::sharedPreferences.isInitialized) {
            initialize(context)
        }
    }

    private fun getClientCredentials(): Pair<String?, String?> {
        val clientId = sharedPreferences.getString("spotify_client_id", BuildConfig.SpotifyClientId)
        val clientSecret = sharedPreferences.getString("spotify_client_secret", BuildConfig.SpotifyClientSecret)
        Log.d(TAG, "Client ID: $clientId, Client Secret: $clientSecret")
        return Pair(clientId, clientSecret)
    }

    fun fetchAccessToken(context: Context, callback: (Boolean) -> Unit) {
        ensureInitialized(context)

        val (clientId, clientSecret) = getClientCredentials()
        if (clientId == null || clientSecret == null) {
            Log.e(TAG, "Client ID or Client Secret is null")
            callback(false)
            return
        }

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
                        Log.e(TAG, "Failed to fetch access token: ${response.message}")
                        callback(false)
                        return
                    }
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Access Token Response: $responseBody")
                    val accessTokenResponse = Gson().fromJson(responseBody, SpotifyAuthResponse::class.java)
                    accessToken = accessTokenResponse.access_token
                    callback(true)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error fetching access token", e)
                callback(false)
            }
        })
    }
}
