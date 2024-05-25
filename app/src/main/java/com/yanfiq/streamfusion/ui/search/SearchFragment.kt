package com.yanfiq.streamfusion.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.databinding.FragmentSearchBinding
import com.yanfiq.streamfusion.ui.search.audius.SearchAudiusFragment
import com.yanfiq.streamfusion.ui.search.youtube.SearchYoutubeFragment

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private lateinit var viewOfLayout: View
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var adapter: SectionPagerAdapter
    private lateinit var viewPager: ViewPager2

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private val TAB_TITLES = arrayOf("Audius", "YouTube")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        viewOfLayout = inflater!!.inflate(R.layout.fragment_search, container, false)
        adapter = SectionPagerAdapter(requireActivity())
        viewPager = viewOfLayout.findViewById(R.id.view_pager)
        viewPager.adapter = adapter
        val tabs: TabLayout = viewOfLayout.findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = TAB_TITLES[position]
            // Inflate custom tab layout
//            val customTabView = LayoutInflater.from(requireContext()).inflate(R.id.tabs, null)

            // Get references to views in custom layout
//            val tabIcon = customTabView.findViewById<ImageView>(R.id.tab_icon)
//            val tabText = customTabView.findViewById<TextView>(R.id.tab_text)

            // Set icon and text for the tab
//            tabIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), TAB_ICON[position]))
//            tabText.text = resources.getString(TAB_TITLES[position])

            // Set custom view for the tab
//            tab.customView = customTabView
        }.attach()
        viewPager.offscreenPageLimit = adapter.itemCount

        searchEditText = viewOfLayout.findViewById(R.id.search_edit_text)
        searchButton = viewOfLayout.findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotBlank()) {
                search(query)
            }
        }

        (activity as AppCompatActivity).supportActionBar?.elevation = 0f
        // Inflate the layout for this fragment
        return viewOfLayout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun search(query: String) {
        var fragmentAudius = adapter.getFragment(0) as SearchAudiusFragment
        fragmentAudius.searchAudius(query)

        var fragmentYoutube = adapter.getFragment(1) as SearchYoutubeFragment
        fragmentYoutube.searchYouTube(query)
    }
}