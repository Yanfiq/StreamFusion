package com.yanfiq.youcloudify.data.response.spotify

import android.util.Base64
import com.google.gson.Gson
import com.yanfiq.streamfusion.data.retrofit.spotify.SpotifyAuthResponse
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

fun getSpotifyAccessToken(clientId: String, clientSecret: String, callback: (String?) -> Unit) {
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
                    callback(null)
                    return
                }
                val responseBody = response.body?.string()
                val accessToken = Gson().fromJson(responseBody, SpotifyAuthResponse::class.java).access_token
                callback(accessToken)
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            callback(null)
        }
    })
}


