package com.yanfiq.streamfusion.data.retrofit.audius

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.yanfiq.streamfusion.domain.model.audius.Track
import com.yanfiq.streamfusion.presentation.viewmodels.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.File
import java.util.concurrent.TimeUnit

object AudiusEndpointUtil {
    private const val ENDPOINTS_FILE_NAME = "endpoints.json"
    private var endpointUsed: String? = null
    private lateinit var endpointsFile: File
    private val gson = Gson()
    private lateinit var trends: List<Track>;

    suspend fun initialize(context: Context, apiStatus: ApiStatus){
        apiStatus.updateAudiusApiReady(newValue = false)
        endpointsFile = File(context.filesDir, ENDPOINTS_FILE_NAME)
        if (!endpointsFile.exists()) {
            val defaultEndpoints = listOf(
                "https://audius-discovery-4.theblueprint.xyz",
                "https://audius-discovery-9.cultur3stake.com",
                "https://blockdaemon-audius-discovery-05.bdnodes.net",
                "https://audius-discovery-12.cultur3stake.com",
                "https://audius-discovery-11.cultur3stake.com",
                "https://audius-metadata-3.figment.io",
                "https://blockchange-audius-discovery-02.bdnodes.net",
                "https://audius-discovery-4.cultur3stake.com",
                "https://audius-discovery-18.cultur3stake.com",
                "https://audius-discovery-14.cultur3stake.com",
                "https://blockdaemon-audius-discovery-02.bdnodes.net",
                "https://blockdaemon-audius-discovery-06.bdnodes.net",
                "https://audius-discovery-1.theblueprint.xyz",
                "https://audius-discovery-13.cultur3stake.com",
                "https://audius-discovery-1.altego.net",
                "https://discovery-au-02.audius.openplayer.org",
                "https://audius-nodes.com",
                "https://audius-discovery-16.cultur3stake.com",
                "https://dn1.matterlightblooming.xyz",
                "https://audius-discovery-3.altego.net",
                "https://audius-discovery-7.cultur3stake.com",
                "https://discoveryprovider2.audius.co",
                "https://audius-discovery-10.cultur3stake.com",
                "https://blockchange-audius-discovery-01.bdnodes.net",
                "https://audius-discovery-3.theblueprint.xyz",
                "https://blockdaemon-audius-discovery-04.bdnodes.net",
                "https://blockchange-audius-discovery-05.bdnodes.net",
                "https://audius-discovery-5.cultur3stake.com",
                "https://dn2.monophonic.digital",
                "https://audius-dp.singapore.creatorseed.com",
                "https://audius-discovery-1.cultur3stake.com",
                "https://audius-discovery-15.cultur3stake.com",
                "https://discovery-us-01.audius.openplayer.org",
                "https://discoveryprovider.audius.co",
                "https://blockchange-audius-discovery-04.bdnodes.net",
                "https://blockdaemon-audius-discovery-01.bdnodes.net",
                "https://audius-discovery-3.cultur3stake.com",
                "https://audius-metadata-5.figment.io",
                "https://audius-discovery-6.cultur3stake.com",
                "https://audius-metadata-4.figment.io",
                "https://dn1.nodeoperator.io",
                "https://dn-usa.audius.metadata.fyi",
                "https://dn1.monophonic.digital",
                "https://audius-discovery-17.cultur3stake.com",
                "https://blockdaemon-audius-discovery-03.bdnodes.net",
                "https://audius.w3coins.io",
                "https://audius-dp.amsterdam.creatorseed.com",
                "https://dn-jpn.audius.metadata.fyi",
                "https://blockchange-audius-discovery-03.bdnodes.net",
                "https://audius-metadata-1.figment.io",
                "https://disc-lon01.audius.hashbeam.com",
                "https://discoveryprovider3.audius.co",
                "https://audius-discovery-2.cultur3stake.com",
                "https://blockdaemon-audius-discovery-08.bdnodes.net",
                "https://audius-discovery-2.theblueprint.xyz",
                "https://audius-discovery-8.cultur3stake.com",
                "https://audius-discovery-2.altego.net",
                "https://audius-metadata-2.figment.io",
                "https://dn1.stuffisup.com")
            updateEndpoints(context, defaultEndpoints)
        }
        fetchEndpoints(context)
        setUsedEndpoint(context)
        apiStatus.updateAudiusApiReady(true)
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

    fun getUsedEndpoint(): String?{
        return endpointUsed
    }

    suspend fun setUsedEndpoint(context: Context){
        endpointUsed = getValidEndpoint(context)
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

        Log.d("AudiusEndpointUtil", "Fastest endpoint: $fastestEndpoint")
        fastestEndpoint
    }

    private suspend fun isEndpointValid(endpoint: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Use OkHttpClient to send a lightweight GET request to the endpoint
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)  // Set a timeout for the connection
                .readTimeout(5, TimeUnit.SECONDS)  // Set a timeout for reading the response
                .build()

            // Construct the request to a lightweight endpoint (e.g., health check)
            val request = Request.Builder()
                .url("$endpoint/health_check")  // Assuming there's a health check endpoint
                .build()

            // Execute the request and check if the response is successful
            client.newCall(request).execute().use { response ->
                return@withContext response.isSuccessful
            }  // Return true if the response status is 2xx
        } catch (e: Exception) {
            return@withContext false// Return false if any exception occurs
        }
    }

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
                    response.body?.use { responseBody ->
                        val newEndpoints = parseEndpoints(responseBody.string())
                        updateEndpoints(context, newEndpoints)
                    }
                } else {
                    Log.d("AudiusEndpointUtil", response.isSuccessful.toString())
                }
            }
        })
    }

    fun getApiInstance(): AudiusApiService? {
        Log.d("AudiusEndpointUtil", "Used endpoints: ${getUsedEndpoint().toString()}")
        return getUsedEndpoint()?.let { AudiusApi.retrofitService(it) }
    }
}