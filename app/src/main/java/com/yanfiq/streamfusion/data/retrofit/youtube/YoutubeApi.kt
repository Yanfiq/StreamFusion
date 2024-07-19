package com.yanfiq.streamfusion.data.retrofit.youtube

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.yanfiq.streamfusion.BuildConfig
import com.yanfiq.streamfusion.dataStore
import com.yanfiq.streamfusion.screens.PreferencesKeys
import kotlinx.coroutines.flow.map
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient

object YouTubeApi {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

//    private fun getEncryptedSharedPreferences(context: Context): EncryptedSharedPreferences {
//        val masterKey = MasterKey.Builder(context)
//            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//            .build()
//
//        return EncryptedSharedPreferences.create(
//            context,
//            "encrypted_prefs",
//            masterKey,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        ) as EncryptedSharedPreferences
//    }

//    private fun getApiKey(context: Context): String? {
//        val youtubeApiKey by (context.dataStore.data.map { preferences ->
//            preferences[PreferencesKeys.YOUTUBE_API_KEY] ?: ""
//        }).collectAsState(initial = )
////        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
////        var api_key = encryptedSharedPreferences.getString("youtube_api_key", BuildConfig.YoutubeApiKey)
//        var api_key = "AIzaSyCIeveSw59h20RI75Bie0F2f0KgaWOFg6E"
//        if (api_key == "") api_key = BuildConfig.YoutubeApiKey
//        Log.d("YoutubeApi", "API key = "+api_key)
//        return api_key
//    }

    fun getApiInstance(context: Context, apiKey: String): YoutubeApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val originalHttpUrl = original.url

                    val url = originalHttpUrl.newBuilder()
                        .addQueryParameter("key", apiKey)
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
