package com.yanfiq.streamfusion.ui.search

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yanfiq.streamfusion.ui.search.audius.SearchAudiusFragment
import com.yanfiq.streamfusion.ui.search.soundcloud.SearchSoundcloudFragment
import com.yanfiq.streamfusion.ui.search.spotify.SearchSpotifyFragment
import com.yanfiq.streamfusion.ui.search.youtube.SearchYoutubeFragment

class SectionPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragmentList = arrayOfNulls<Fragment>(4)

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = SearchAudiusFragment()
            1 -> fragment = SearchSpotifyFragment()
            2 -> fragment = SearchSoundcloudFragment()
            3 -> fragment = SearchYoutubeFragment()
        }
        fragmentList[position] = fragment
        return fragment as Fragment
    }

    fun getFragment(position: Int): Fragment? {
        return fragmentList[position]
    }
}