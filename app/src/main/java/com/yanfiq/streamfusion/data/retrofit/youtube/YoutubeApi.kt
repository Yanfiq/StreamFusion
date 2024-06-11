package com.yanfiq.streamfusion.data.retrofit.youtube

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.yanfiq.streamfusion.BuildConfig
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient

object YouTubeApi {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    private fun getEncryptedSharedPreferences(context: Context): EncryptedSharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "encrypted_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    private fun getApiKey(context: Context): String? {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        return encryptedSharedPreferences.getString("youtube_api_key", BuildConfig.YoutubeApiKey)
    }

    fun getApiInstance(context: Context): YoutubeApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val originalHttpUrl = original.url

                    val url = originalHttpUrl.newBuilder()
                        .addQueryParameter("key", getApiKey(context))
                        .build()

                    val requestBuilder = original.newBuilder().url(url)
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YoutubeApiService::class.java)
    }
}
