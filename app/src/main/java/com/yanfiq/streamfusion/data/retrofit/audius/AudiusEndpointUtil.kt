package com.yanfiq.streamfusion.data.retrofit.audius

import android.util.Log
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusApi
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointsApi
import com.yanfiq.streamfusion.data.response.audius.EndpointsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AudiusEndpointUtil {

    private var endpoints: List<String>? = null

    fun fetchEndpoints(callback: (List<String>?) -> Unit) {
        if (endpoints != null) {
            callback(endpoints)
            return
        }

        val api = AudiusEndpointsApi.create()
        api.getEndpoints().enqueue(object : Callback<EndpointsResponse> {
            override fun onResponse(call: Call<EndpointsResponse>, response: Response<EndpointsResponse>) {
                if (response.isSuccessful) {
                    endpoints = response.body()?.data
                    callback(endpoints)
                } else {
                    Log.e("AudiusEndpointUtil", "Failed to fetch endpoints: ${response.errorBody()?.string()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<EndpointsResponse>, t: Throwable) {
                Log.e("AudiusEndpointUtil", "Failed to fetch endpoints: ${t.message}")
                callback(null)
            }
        })
    }

    fun getApiInstance(callback: (AudiusApiService?) -> Unit) {
        fetchEndpoints { endpoints ->
            if (endpoints != null && endpoints.isNotEmpty()) {
                val api = AudiusApi.retrofitService(endpoints[0]) // Use the first endpoint
                callback(api)
            } else {
                callback(null)
            }
        }
    }
}

