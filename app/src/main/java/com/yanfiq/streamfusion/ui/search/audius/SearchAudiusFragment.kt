package com.yanfiq.streamfusion.ui.search.audius

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
import com.yanfiq.streamfusion.data.response.audius.AudiusResponse
import com.yanfiq.streamfusion.data.response.audius.Track
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusApi
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var recyclerView: RecyclerView
private lateinit var adapter: TrackAdapter
private lateinit var viewOfLayout: View

/**
 * A simple [Fragment] subclass.
 * Use the [SearchAudiusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchAudiusFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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

        viewOfLayout = inflater!!.inflate(R.layout.fragment_search_audius, container, false)
        // Inflate the layout for this fragment
        recyclerView = viewOfLayout.findViewById(R.id.recycler_view_audius)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TrackAdapter(emptyList())
        recyclerView.adapter = adapter

        return viewOfLayout
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchAudiusFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchAudiusFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    public fun searchAudius(query: String) {
        AudiusEndpointUtil.getApiInstance { api ->
            if (api != null) {
                api.searchTracks(query).enqueue(object : Callback<AudiusResponse> {
                    override fun onResponse(call: Call<AudiusResponse>, response: Response<AudiusResponse>) {
                        if (response.isSuccessful) {
                            val tracks = response.body()?.data ?: emptyList()
                            adapter = TrackAdapter(tracks)
                            recyclerView.adapter = adapter
                            adapter.setOnItemClickCallback(object : TrackAdapter.OnItemClickCallback {
                                override fun onItemClicked(data: Track) {
                                    Toast.makeText(context, data.title, Toast.LENGTH_SHORT).show()
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
            } else {
                Log.e("AudiusSearchFragment", "No working endpoint found")
            }
        }
    }

    private fun play(track: Track){
        val explicitIntent = Intent(requireActivity(), PlayAudiusActivity::class.java)
        explicitIntent.putExtra("TRACK_ID", track.id)
        startActivity(explicitIntent)
    }
}