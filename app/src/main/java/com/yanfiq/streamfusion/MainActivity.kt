package com.yanfiq.streamfusion

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.yanfiq.streamfusion.data.remote.AudiusRemoteDataSource
import com.yanfiq.streamfusion.data.remote.SoundcloudRemoteDataSource
import com.yanfiq.streamfusion.data.remote.SpotifyRemoteDataSource
import com.yanfiq.streamfusion.data.remote.YoutubeRemoteDataSource
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import com.yanfiq.streamfusion.presentation.viewmodels.ApiStatus
import com.yanfiq.streamfusion.presentation.viewmodels.SearchResult
import com.yanfiq.streamfusion.presentation.viewmodels.SearchStatus
import com.yanfiq.streamfusion.presentation.viewmodels.TrendingSongs
import com.yanfiq.streamfusion.domain.model.Track
import com.yanfiq.streamfusion.domain.usecase.SearchUseCase
import com.yanfiq.streamfusion.presentation.screens.BottomNavigationBar
import com.yanfiq.streamfusion.presentation.screens.search.SearchData
import com.yanfiq.streamfusion.presentation.screens.search.SearchScreenData
import com.yanfiq.streamfusion.presentation.ui.theme.AppTheme
import com.yanfiq.streamfusion.presentation.viewmodels.SearchViewModel
import com.yanfiq.streamfusion.presentation.viewmodels.SearchViewModelFactory
import com.yanfiq.streamfusion.utils.ISODurationToSeconds
import com.yanfiq.streamfusion.utils.getHiddenMessage
import com.yanfiq.streamfusion.utils.loadImageFromDrawable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private val apiStatus: ApiStatus by viewModels()
    private val trendingSongs: TrendingSongs by viewModels()
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            AudiusEndpointUtil.initialize(this@MainActivity, apiStatus)
        }

        val searchUseCase = SearchUseCase()

        val factory = SearchViewModelFactory(searchUseCase)
        searchViewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)

        setContent{
            MainActivityView()
        }
    }

    @Composable
    fun MainActivityView() {
        val searchScreenData by searchViewModel.searchScreenData.collectAsState()
        MainScreen(searchScreenData = searchScreenData)
    }

    @Composable
    fun MainScreen(searchScreenData: SearchScreenData){
        AppTheme {
            Column {
                BottomNavigationBar(
                    searchScreenData = searchScreenData,
                    onSearchClick = {searchQuery ->  lifecycleScope.launch{searchViewModel.startSearch(searchQuery,this@MainActivity, apiStatus)}},
                    onPlayClick = {track, streamingPlatform -> searchViewModel.playTrack(this@MainActivity, track, streamingPlatform) }
                )
            }
        }
    }

    @Composable
    @Preview
    private fun MainScreenPreview(){
        val tracks = listOf(
            Track("", "Test1", "Test1", 63, ""),
            Track("", "Test2", "Test2", 100, ""),
            Track("", "Test3", "Test3", 100, "")
        )
        val audiusSearchData = SearchData(tracks, false, "")
        val soundcloudSearchData = SearchData(tracks, false, "")
        val spotifySearchData = SearchData(tracks, false, "")
        val youtubeSearchData = SearchData(tracks, false, "")
        val searchScreenData = SearchScreenData(audiusSearchData, soundcloudSearchData, spotifySearchData, youtubeSearchData)
        MainScreen(searchScreenData)
    }
}