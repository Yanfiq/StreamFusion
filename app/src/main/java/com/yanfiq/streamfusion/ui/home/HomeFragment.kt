package com.yanfiq.streamfusion.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yanfiq.streamfusion.data.response.audius.AudiusResponse
import com.yanfiq.streamfusion.data.response.audius.Track
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusApi
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import com.yanfiq.streamfusion.databinding.FragmentHomeBinding
import com.yanfiq.streamfusion.ui.home.TrackAdapter
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var audius_trends: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var audius_btn: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        audius_btn = binding.audiusTrendsBtn
        audius_trends = binding.audiusTrends
        audius_trends.layoutManager = LinearLayoutManager(context)
        adapter = TrackAdapter(emptyList())
        audius_trends.adapter = adapter
        audius_trends.visibility = View.GONE

        audius_btn.setOnClickListener {
            if (audius_trends.visibility == View.VISIBLE) {
                audius_trends.visibility = View.GONE
            } else {
                audius_trends.visibility = View.VISIBLE
                if (AudiusEndpointUtil.getUsedEndpoint() != null) {
                    fetchTrendingTracks()
                }
            }
        }

        return binding.root
    }

    fun fetchTrendingTracks() {
        val usedEndpoint = AudiusEndpointUtil.getUsedEndpoint()
        if (usedEndpoint != null) {
            if(AudiusEndpointUtil.getApiInstance() != null){
                val api = AudiusEndpointUtil.getApiInstance()
                api?.getTrendingTracks()?.enqueue(object : Callback<AudiusResponse> {
                    override fun onResponse(call: Call<AudiusResponse>, response: Response<AudiusResponse>) {
                        if (response.isSuccessful) {
                            val tracks = response.body()?.data ?: emptyList()
                            updateRecyclerView(tracks)
                        } else {
                            Log.d("AudiusTrends", "Response not successful: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<AudiusResponse>, t: Throwable) {
                        Log.d("AudiusTrends", "API call failed: ${t.message}")
                    }
                })
            }
        } else {
            Log.d("AudiusTrends", "No valid endpoint found")
        }
    }

    private fun updateRecyclerView(tracks: List<Track>) {
        activity?.runOnUiThread {
            adapter = TrackAdapter(tracks)
            audius_trends.adapter = adapter
            adapter.setOnItemClickCallback(object : TrackAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Track) {
                    Toast.makeText(context, data.id, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
