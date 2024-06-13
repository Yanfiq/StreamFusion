package com.yanfiq.streamfusion.ui.search.youtube

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yanfiq.streamfusion.BuildConfig
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.data.response.youtube.Video
import com.yanfiq.streamfusion.data.response.youtube.VideoItem
import com.yanfiq.streamfusion.data.response.youtube.YouTubeResponse
import com.yanfiq.streamfusion.data.retrofit.youtube.YouTubeApi
import com.yanfiq.streamfusion.ui.youtube.VideoAdapter
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class SearchYoutubeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoAdapter
    private lateinit var viewOfLayout: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        viewOfLayout = inflater.inflate(R.layout.fragment_search_youtube, container, false) as ViewGroup
        recyclerView = viewOfLayout.findViewById(R.id.recycler_view_youtube)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = VideoAdapter(emptyList())
        recyclerView.adapter = adapter

        return viewOfLayout
    }

    fun searchYouTube(query: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val limit = sharedPreferences.getString("result_per_query", "10")!!.toInt()
        lifecycleScope.launch {
            try {
                val youTubeApiService = YouTubeApi.getApiInstance(requireContext())
                val searchResponse = youTubeApiService.searchVideos("snippet", query, "video", limit)
                if (searchResponse.isSuccessful) {
                    val videoItems = searchResponse.body()?.items
                    val videoIds = videoItems?.joinToString(",") { it.id.videoId }
                    Log.d("searchYoutubeFragment", videoIds.toString())

                    val detailsResponse = videoIds?.let {
                        youTubeApiService.getVideoDetails("contentDetails",
                            it)
                    }
                    if (detailsResponse != null) {
                        if (detailsResponse.isSuccessful) {
                            showOriginalLayout()

                            val videoDetailsItems = detailsResponse.body()?.items ?: emptyList()

                            val videos = videoItems.map { videoItem ->
                                val detailsItem = videoDetailsItems.find { it.id == videoItem.id.videoId }
                                Video(
                                    id = videoItem.id.videoId,
                                    title = videoItem.snippet.title,
                                    description = videoItem.snippet.description,
                                    thumbnailUrl = videoItem.snippet.thumbnails.default.url,
                                    duration = detailsItem?.contentDetails?.duration ?: ""
                                )
                            }
                            val listVideoAdapter = VideoAdapter(videos)
                            recyclerView.adapter = listVideoAdapter
                            listVideoAdapter.setOnItemClickCallback(object :
                                VideoAdapter.OnItemClickCallback {
                                override fun onItemClicked(data: Video) {
                                    Toast.makeText(context, data.id, Toast.LENGTH_SHORT).show()
                                    play(data)
                                }
                            })
                        } else {
                            showErrorLayout()
                            Log.d("YoutubeSearchFragment", "Response not successful: ${detailsResponse.errorBody()?.string()}")
                        }
                    }
                } else {
                    showErrorLayout()
                    Log.d("YoutubeSearchFragment", "API call failed: ${searchResponse.message()}")
                }
            } catch (e: Exception) {
                showErrorLayout()
                Log.d("YoutubeSearchFragment", "API call failed: ${e}")
            }
        }
    }

    private fun showOriginalLayout() {
        // Inflate the original layout
        val inflater = LayoutInflater.from(context)
        val originalLayout = inflater.inflate(R.layout.fragment_search_youtube, viewOfLayout, false)

        // Replace the current view with the original layout
        viewOfLayout.removeAllViews()
        viewOfLayout.addView(originalLayout)

        // Reinitialize RecyclerView and other UI components
        recyclerView = originalLayout.findViewById(R.id.recycler_view_youtube)
        // Initialize RecyclerView with data
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = VideoAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    private fun showErrorLayout() {
        // Inflate the error layout
        val inflater = LayoutInflater.from(context)
        val errorLayout = inflater.inflate(R.layout.layout_api_call_failed, viewOfLayout, false)

        // Replace the current view with the error layout
        viewOfLayout.removeAllViews()
        viewOfLayout.addView(errorLayout)
    }

    private fun play(data: Video){
        val explicitIntent = Intent(requireActivity(), PlayYoutubeActivity::class.java)
        explicitIntent.putExtra("VIDEO_ID", data.id)
        explicitIntent.putExtra("VIDEO_TITLE", data.title)
        explicitIntent.putExtra("VIDEO_DURATION", data.duration)
        startActivity(explicitIntent)
    }
}