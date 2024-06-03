package com.yanfiq.streamfusion.data.retrofit.audius

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.yanfiq.streamfusion.data.response.audius.AudiusResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.File

object AudiusEndpointUtil {
    private const val ENDPOINTS_FILE_NAME = "endpoints.json"
    private lateinit var endpointUsed: String
    private lateinit var endpointsFile: File
    private val client = OkHttpClient()
    private val gson = Gson()

    fun initialize(context: Context){
        endpointsFile = File(context.filesDir, ENDPOINTS_FILE_NAME)
        if (!endpointsFile.exists()) {
            val defaultEndpoints = listOf("https://audius-discovery-14.cultur3stake.com",
                "https://blockdaemon-audius-discovery-03.bdnodes.net",
                "https://audius-discovery-3.cultur3stake.com",
                "https://audius-dp.singapore.creatorseed.com",
                "https://audius-discovery-7.cultur3stake.com",
                "https://blockdaemon-audius-discovery-06.bdnodes.net",
                "https://audius-metadata-2.figment.io",
                "https://audius-discovery-4.theblueprint.xyz",
                "https://audius-discovery-4.cultur3stake.com",
                "https://blockdaemon-audius-discovery-08.bdnodes.net",
                "https://audius-discovery-1.altego.net",
                "https://discovery-au-02.audius.openplayer.org",
                "https://dn1.matterlightblooming.xyz",
                "https://audius-discovery-3.altego.net",
                "https://audius-discovery-5.cultur3stake.com",
                "https://audius-metadata-4.figment.io",
                "https://blockdaemon-audius-discovery-05.bdnodes.net",
                "https://audius-discovery-3.theblueprint.xyz",
                "https://blockchange-audius-discovery-02.bdnodes.net",
                "https://discoveryprovider.audius.co",
                "https://audius.w3coins.io",
                "https://blockchange-audius-discovery-03.bdnodes.net",
                "https://dn1.stuffisup.com",
                "https://audius-discovery-13.cultur3stake.com",
                "https://blockdaemon-audius-discovery-01.bdnodes.net",
                "https://audius-nodes.com",
                "https://dn1.nodeoperator.io",
                "https://dn2.monophonic.digital",
                "https://audius-discovery-9.cultur3stake.com",
                "https://audius-discovery-1.theblueprint.xyz",
                "https://blockdaemon-audius-discovery-02.bdnodes.net",
                "https://dn-usa.audius.metadata.fyi",
                "https://blockchange-audius-discovery-01.bdnodes.net",
                "https://audius-discovery-16.cultur3stake.com",
                "https://dn1.monophonic.digital",
                "https://discoveryprovider3.audius.co",
                "https://audius-discovery-15.cultur3stake.com",
                "https://dn-jpn.audius.metadata.fyi",
                "https://audius-discovery-18.cultur3stake.com",
                "https://discoveryprovider2.audius.co",
                "https://disc-lon01.audius.hashbeam.com",
                "https://blockdaemon-audius-discovery-04.bdnodes.net",
                "https://audius-discovery-6.cultur3stake.com",
                "https://blockchange-audius-discovery-04.bdnodes.net",
                "https://audius-discovery-10.cultur3stake.com",
                "https://discovery-us-01.audius.openplayer.org",
                "https://audius-metadata-1.figment.io",
                "https://audius-discovery-8.cultur3stake.com",
                "https://audius-discovery-12.cultur3stake.com",
                "https://audius-metadata-3.figment.io",
                "https://audius-discovery-2.cultur3stake.com",
                "https://audius-discovery-2.altego.net",
                "https://audius-dp.amsterdam.creatorseed.com",
                "https://audius-metadata-5.figment.io",
                "https://audius-discovery-11.cultur3stake.com",
                "https://audius-discovery-2.theblueprint.xyz",
                "https://audius-discovery-1.cultur3stake.com")
            updateEndpoints(context, defaultEndpoints)
        }
        endpointUsed = "null"
    }

    fun getEndpoints(context: Context): List<String> {
        val endpointsFile = File(context.filesDir, ENDPOINTS_FILE_NAME)
        return try {
            val json = endpointsFile.readText()
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
        } catch (e: IOException) {
            emptyList()
        }
    }

    fun updateEndpoints(context: Context, newEndpoints: List<String>) {
        val endpointsFile = File(context.filesDir, ENDPOINTS_FILE_NAME)
        val json = gson.toJson(newEndpoints)
        endpointsFile.writeText(json)
    }


    private fun parseEndpoints(json: String?): List<String> {
        val jsonObject = gson.fromJson(json, JsonObject::class.java)
        return gson.fromJson(jsonObject["data"], object : TypeToken<List<String>>() {}.type)
    }

    fun getUsedEndpoint(): String{
        return endpointUsed
    }

    suspend fun setUsedEndpoint(context: Context){
        endpointUsed = getValidEndpoint(context).toString()
    }

    suspend fun getValidEndpoint(context: Context): String? = withContext(Dispatchers.IO) {
        val endpoints = getEndpoints(context)
        var fastestEndpoint: String? = null
        var fastestResponseTime = Long.MAX_VALUE

        val deferredResults = endpoints.map { endpoint ->
            async {
                val startTime = System.currentTimeMillis()
                val isValid = isEndpointValid(endpoint)
                val responseTime = System.currentTimeMillis() - startTime
                Pair(endpoint, if (isValid) responseTime else Long.MAX_VALUE)
            }
        }

        deferredResults.awaitAll().forEach { (endpoint, responseTime) ->
            if (responseTime < fastestResponseTime) {
                fastestResponseTime = responseTime
                fastestEndpoint = endpoint
            }
        }

        fastestEndpoint
    }
    private suspend fun isEndpointValid(endpoint: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val api = AudiusApi.retrofitService(endpoint)
            val response: Response<*> = api.searchTracks("Xenogenesis").execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

//    private fun isEndpointValid(endpoint: String): Boolean {
//        var validity: Boolean = false
//        val api = AudiusApi.retrofitService(endpoint)
//        api.searchTracks("Xenogenesis").enqueue(object : Callback<AudiusResponse> {
//            override fun onResponse(call: Call<AudiusResponse>, response: Response<AudiusResponse>) {
//                if (response.isSuccessful) {
//                    validity = true
//                } else {
//                    validity = false
//                    Log.d("AudiusEndpointUtil", "Response not successful: ${response.errorBody()?.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<AudiusResponse>, t: Throwable) {
//                Log.d("AudiusEndpointUtil", "API call failed: ${t.message}")
//            }
//        })
//        return validity
//    }

    fun fetchEndpoints(context: Context) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.audius.co")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("AudiusEndpointUtil", "Failed to fetch endpoints: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        val newEndpoints = parseEndpoints(responseBody)
                        updateEndpoints(context, newEndpoints)
                    }
                } else {
                    Log.d("AudiusEndpointUtil", response.isSuccessful.toString())
                }
            }
        })
    }

    fun getApiInstance(): AudiusApiService {
        return AudiusApi.retrofitService(getUsedEndpoint())
    }
}