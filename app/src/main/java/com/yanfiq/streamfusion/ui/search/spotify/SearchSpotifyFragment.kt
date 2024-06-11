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
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.data.retrofit.spotify.SpotifyApi
import com.yanfiq.streamfusion.data.response.spotify.SpotifySearchResponse
import com.yanfiq.streamfusion.data.response.spotify.Track
import com.yanfiq.streamfusion.ui.youtube.VideoAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchSpotifyFragment : Fragment() {
    private lateinit var viewOfLayout: ViewGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        viewOfLayout = inflater.inflate(R.layout.fragment_search_spotify, container, false) as ViewGroup

        recyclerView = viewOfLayout.findViewById(R.id.recycler_view_spotify)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = TrackAdapter(emptyList())
        recyclerView.adapter = adapter

        SpotifyApi.initialize(requireContext())

        return viewOfLayout
    }

    fun searchSpotify(query: String) {
        SpotifyApi.fetchAccessToken(requireContext()) { success ->
            activity?.runOnUiThread {
                if (success) {
                    SpotifyApi.service.searchTracks(query, "track").enqueue(object : Callback<SpotifySearchResponse> {
                        override fun onResponse(call: Call<SpotifySearchResponse>, response: Response<SpotifySearchResponse>) {
                            activity?.runOnUiThread {
                                if (response.isSuccessful) {
                                    showOriginalLayout()
                                    val tracks = response.body()?.tracks?.items ?: emptyList()
                                    adapter = TrackAdapter(tracks)
                                    recyclerView.adapter = adapter
                                    adapter.setOnItemClickCallback(object : TrackAdapter.OnItemClickCallback {
                                        override fun onItemClicked(data: Track) {
                                            Toast.makeText(context, data.name, Toast.LENGTH_SHORT).show()
                                            val spotifyEmbedUrl = "https://open.spotify.com/embed/track/" + data.id
                                            val intent = Intent(context, PlaySpotifyActivity::class.java)
                                            intent.putExtra("SPOTIFY_EMBED_URL", spotifyEmbedUrl)
                                            startActivity(intent)
                                        }
                                    })
                                } else {
                                    showErrorLayout()
                                }
                            }
                        }

                        override fun onFailure(call: Call<SpotifySearchResponse>, t: Throwable) {
                            activity?.runOnUiThread {
                                showErrorLayout()
                            }
                        }
                    })
                } else {
                    showErrorLayout()
                }
            }
        }
    }

    private fun showOriginalLayout() {
        val inflater = LayoutInflater.from(context)
        val originalLayout = inflater.inflate(R.layout.fragment_search_spotify, viewOfLayout, false)

        viewOfLayout.removeAllViews()
        viewOfLayout.addView(originalLayout)

        recyclerView = originalLayout.findViewById(R.id.recycler_view_spotify)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = TrackAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    private fun showErrorLayout() {
        val inflater = LayoutInflater.from(context)
        val errorLayout = inflater.inflate(R.layout.layout_api_call_failed, viewOfLayout, false)

        viewOfLayout.removeAllViews()
        viewOfLayout.addView(errorLayout)
    }
}
