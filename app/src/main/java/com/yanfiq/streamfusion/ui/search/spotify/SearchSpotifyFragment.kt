package com.yanfiq.streamfusion.ui.search.spotify

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
import com.yanfiq.streamfusion.data.retrofit.spotify.SpotifyApi
import com.yanfiq.streamfusion.data.retrofit.youtube.YouTubeApi
import com.yanfiq.streamfusion.ui.search.youtube.PlayYoutubeActivity
import com.yanfiq.streamfusion.ui.youtube.VideoAdapter
import com.yanfiq.youcloudify.data.response.spotify.SpotifySearchResponse
import com.yanfiq.youcloudify.data.response.spotify.Track
import com.yanfiq.youcloudify.data.response.spotify.getSpotifyAccessToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchSpotifyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchSpotifyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewOfLayout: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var accessToken: String

    private val clientId = BuildConfig.SpotifyClientId
    private val clientSecret = BuildConfig.SpotifyClientSecret
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
        viewOfLayout = inflater.inflate(R.layout.fragment_search_spotify, container, false)

        recyclerView = viewOfLayout.findViewById(R.id.recycler_view_spotify)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = TrackAdapter(emptyList())
        recyclerView.adapter = adapter


        getSpotifyAccessToken(clientId, clientSecret) { token ->
            if (token != null) {
                accessToken = token
            } else {
                Toast.makeText(context, "Failed to get access token", Toast.LENGTH_SHORT).show()
            }
        }

        return viewOfLayout
    }

    fun searchSpotify(query: String) {
        SpotifyApi.service.searchTracks(query, "track", "Bearer $accessToken")
            .enqueue(object : Callback<SpotifySearchResponse> {
                override fun onResponse(call: Call<SpotifySearchResponse>, response: Response<SpotifySearchResponse>) {
                    if (response.isSuccessful) {
                        val tracks = response.body()?.tracks?.items ?: emptyList()
                        adapter = TrackAdapter(tracks)
                        recyclerView.adapter = adapter
                        adapter.setOnItemClickCallback(object : TrackAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: Track) {
                                Toast.makeText(context, data.name, Toast.LENGTH_SHORT).show()
                                val spotifyEmbedUrl = "https://open.spotify.com/embed/track/"+data.id
                                val intent = Intent(context, PlaySpotifyActivity::class.java)
                                intent.putExtra("SPOTIFY_EMBED_URL", spotifyEmbedUrl)
                                startActivity(intent)
                            }
                        })
                    }
                }

                override fun onFailure(call: Call<SpotifySearchResponse>, t: Throwable) {
                    // Handle error
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
         * @return A new instance of fragment SearchSpotifyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchSpotifyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}