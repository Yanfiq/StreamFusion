package com.yanfiq.streamfusion.ui.search.youtube

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yanfiq.streamfusion.BuildConfig
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.data.response.youtube.VideoItem
import com.yanfiq.streamfusion.data.response.youtube.YouTubeResponse
import com.yanfiq.streamfusion.data.retrofit.youtube.YouTubeApi
import com.yanfiq.streamfusion.ui.youtube.VideoAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchYoutubeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchYoutubeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoAdapter
    private lateinit var viewOfLayout: View
    private val apiKey = BuildConfig.YoutubeApiKey

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        viewOfLayout = inflater!!.inflate(R.layout.fragment_search_youtube, container, false)
        recyclerView = viewOfLayout.findViewById(R.id.recycler_view_youtube)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = VideoAdapter(emptyList())
        recyclerView.adapter = adapter

        // Inflate the layout for this fragment
        return viewOfLayout
    }

    public fun searchYouTube(query: String) {
        YouTubeApi.retrofitService.searchVideos("snippet", query, "video", apiKey)
            .enqueue(object : Callback<YouTubeResponse> {
                override fun onResponse(
                    call: Call<YouTubeResponse>,
                    response: Response<YouTubeResponse>
                ) {
                    if (response.isSuccessful) {
                        val videos = response.body()?.items ?: emptyList()
                        val listVideoAdapter = VideoAdapter(videos)
                        recyclerView.adapter = listVideoAdapter
                        listVideoAdapter.setOnItemClickCallback(object :
                            VideoAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: VideoItem) {
                                Toast.makeText(context, data.id.videoId, Toast.LENGTH_SHORT).show()
                                play(data)
                            }
                        })
                    }
                    else{
                        Log.d("YoutubeSearchFragment", "Response not successful: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<YouTubeResponse>, t: Throwable) {
                    Log.d("YoutubeSearchFragment", "API call failed: ${t.message}")
                }
            })
    }

    private fun play(data: VideoItem){
        val explicitIntent = Intent(requireActivity(), PlayYoutubeActivity::class.java)
        explicitIntent.putExtra("VIDEO_ID", data.id.videoId)
        startActivity(explicitIntent)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchYoutubeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchYoutubeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}