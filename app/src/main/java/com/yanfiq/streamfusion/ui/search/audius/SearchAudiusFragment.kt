package com.yanfiq.streamfusion.ui.search.audius

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.data.response.audius.AudiusResponse
import com.yanfiq.streamfusion.data.response.audius.Track
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var recyclerView: RecyclerView
private lateinit var adapter: TrackAdapter
private lateinit var viewOfLayout: View

class SearchAudiusFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        viewOfLayout = inflater.inflate(R.layout.fragment_search_audius, container, false)
        recyclerView = viewOfLayout.findViewById(R.id.recycler_view_audius)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TrackAdapter(emptyList())
        recyclerView.adapter = adapter

        return viewOfLayout
    }

    fun searchAudius(query: String) {
        if(AudiusEndpointUtil.getUsedEndpoint() != null){
            val api = AudiusEndpointUtil.getApiInstance()
            if (api != null) {
                api.searchTracks(query).enqueue(object : Callback<AudiusResponse> {
                    override fun onResponse(call: Call<AudiusResponse>, response: Response<AudiusResponse>) {
                        if (response.isSuccessful) {
                            val tracks = response.body()?.data ?: emptyList()
                            adapter = TrackAdapter(tracks)
                            recyclerView.adapter = adapter
                            adapter.setOnItemClickCallback(object : TrackAdapter.OnItemClickCallback {
                                override fun onItemClicked(data: Track) {
                                    Toast.makeText(context, data.id, Toast.LENGTH_SHORT).show()
                                    play(data)
                                }
                            })
                        } else {
                            Log.d("AudiusSearchFragment", "Response not successful: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<AudiusResponse>, t: Throwable) {
                        Log.d("AudiusSearchFragment", "API call failed: ${t.message}")
                    }
                })
            }
        }
//        lifecycleScope.launch{
//
//        }
    }

    private fun play(track: Track){
        val explicitIntent = Intent(requireActivity(), PlayAudiusActivity::class.java)
        explicitIntent.putExtra("TRACK_ID", track.id)
        explicitIntent.putExtra("TRACK_TITLE", track.title)
        explicitIntent.putExtra("TRACK_ARTWORK", track.artwork.medium)
        startActivity(explicitIntent)
    }
}