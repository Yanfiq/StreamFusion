package com.yanfiq.streamfusion.presentation.screens.search

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.data.viewmodel.ApiStatus
import com.yanfiq.streamfusion.data.viewmodel.SearchResult
import com.yanfiq.streamfusion.data.viewmodel.SearchStatus
import com.yanfiq.streamfusion.dataStore
import com.yanfiq.streamfusion.presentation.screens.settings.PreferencesKeys
import com.yanfiq.streamfusion.presentation.ui.theme.AppTheme
import com.yanfiq.streamfusion.utils.getHiddenMessage
import com.yanfiq.streamfusion.utils.loadImageFromDrawable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(searchResult: SearchResult, searchStatus: SearchStatus, apiStatus: ApiStatus, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var searchInput by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val isAudiusSearching by searchStatus.audiusSearchStatus.observeAsState(initial = false)
    val isSoundcloudSearching by searchStatus.soundcloudSearchStatus.observeAsState(initial = false)
    val isYoutubeSearching by searchStatus.youtubeSearchStatus.observeAsState(initial = false)

    val isAudiusReady by apiStatus.audiusApiReady.observeAsState(initial = false)
    val maxResult by (LocalContext.current.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.RESULT_PER_SEARCH] ?: 10f
    }).collectAsState(initial = 10f)

    val youtubeApiKey by (LocalContext.current.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.YOUTUBE_API_KEY] ?: getHiddenMessage(loadImageFromDrawable(context, R.drawable.youtubelogo_text_nodpi))
    }).collectAsState(initial = "")

    val spotifyHidden = getHiddenMessage(loadImageFromDrawable(context, R.drawable.spotifylogo_nodpi))

    val spotifyClientId by (LocalContext.current.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SPOTIFY_CLIENT_ID] ?: spotifyHidden.substring(0, spotifyHidden.indexOf('|'))
    }).collectAsState(initial = "")

    val spotifyClientSecret by (LocalContext.current.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SPOTIFY_CLIENT_SECRET] ?: spotifyHidden.substring(spotifyHidden.indexOf('|')+1)
    }).collectAsState(initial = "")

    // Perform the search when both conditions are met
    if (isAudiusSearching && isAudiusReady) {
        Log.d("AudiusSearch", "Starting search: ${searchQuery}")
        searchAudius(searchQuery, maxResult.toInt(), context, apiStatus) { result ->
            searchResult.updateAudiusSearchData(result)
            searchStatus.updateAudiusSearchStatus(false)
        }
    }

    AppTheme {
        Scaffold (
            topBar = {
                TopAppBar(
                    title = { Text("Search") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            content = {padding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize())  {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = searchInput,
                                onValueChange = { searchInput = it },
                                placeholder = { Text(text = "Keyword") },
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent
                                )
                            )
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    searchQuery = searchInput

                                    //soundcloud
                                    searchStatus.updateSoundcloudSearchStatus(true)
                                    Log.d("SoundcloudSearch", "Starting search: ${searchQuery}")
                                    searchSoundcloud(
                                        context = context,
                                        query = searchQuery,
                                        limit = maxResult.toInt()
                                    ) { result ->
                                        searchResult.updateSoundcloudSearchData(result)
                                        searchStatus.updateSoundcloudSearchStatus(false)
                                    }

                                    Log.d("Audius search", if(isAudiusReady) "Ready" else "Not ready")
                                    //audius
                                    searchStatus.updateAudiusSearchStatus(true)
                                    if (!isAudiusReady) {
                                        searchStatus.setPendingSearchQuery(searchQuery)
                                        Log.d("AudiusSearch", "Pending search: ${searchQuery}")
                                    }

                                    //spotify
                                    searchSpotify(context, searchQuery, maxResult.toInt(), spotifyClientId, spotifyClientSecret){result ->
                                        searchResult.updateSpotifySearchData(result)
                                    }

                                    //youtube
                                    searchStatus.updateYoutubeSearchStatus(true)
                                    if(youtubeApiKey != ""){
                                        coroutineScope.launch {
                                            searchYouTube(context, searchQuery, maxResult.toInt(), youtubeApiKey){response ->
                                                searchResult.updateYoutubeSearchData(response)
                                                searchStatus.updateYoutubeSearchStatus(false)
                                            }
                                        }
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Icon")
                                Text(text = "Search")
                            }

                            SearchTabLayout(searchResult = searchResult, searchStatus = searchStatus, apiStatus = apiStatus, context = context, searchQuery = searchQuery)
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchTabLayout(searchResult: SearchResult, searchStatus: SearchStatus, apiStatus: ApiStatus, context: Context, searchQuery: String) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = {4})
    val coroutineScope = rememberCoroutineScope()

    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            selectedTabIndex = pagerState.currentPage
            for (i in 0..pagerState.pageCount) {
                Tab(
                    selected = selectedTabIndex == i,
                    onClick = {
                        selectedTabIndex = i
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(i)
                        }
                        },
                    text = {
                        val dataStore: DataStore<Preferences> = LocalContext.current.dataStore
                        val isDark by (dataStore.data.map { preferences ->
                            preferences[PreferencesKeys.DARK_MODE] ?: false
                        }).collectAsState(initial = isSystemInDarkTheme())

                        when (i){
                            0 -> Icon(painter = painterResource(id = R.drawable.audiuslogo), modifier = Modifier.height(25.dp), contentDescription = "Audius logo", tint = if (!isDark) Color.Unspecified else MaterialTheme.colorScheme.onSurface)
                            1 -> Icon(painter = painterResource(id = R.drawable.soundcloudlogo), modifier = Modifier.height(25.dp), contentDescription = "SoundCloud logo", tint = MaterialTheme.colorScheme.onSurface)
                            2 -> Icon(painter = painterResource(id = R.drawable.spotifylogo), modifier = Modifier.height(25.dp), contentDescription = "Spotify logo", tint = Color.Unspecified)
                            3 -> Row {
                                    Icon(painter = painterResource(id = R.drawable.youtubelogo_logo), modifier = Modifier.height(25.dp), contentDescription = "YouTube logo", tint = Color.Unspecified)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Icon(painter = painterResource(id = R.drawable.youtubelogo_text), modifier = Modifier.height(25.dp), contentDescription = "YouTube logo", tint = MaterialTheme.colorScheme.onSurface)
                                 }
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> AudiusSearchResult(apiStatus = apiStatus, searchResult = searchResult, searchStatus = searchStatus, context = context, searchQuery = searchQuery)
                1 -> SoundcloudSearchResult(searchResult = searchResult, searchStatus = searchStatus, context = context, searchQuery = searchQuery)
                2 -> SpotifySearchResult(searchResult = searchResult, searchStatus = searchStatus, context = context)
                3 -> YoutubeSearchResult(searchResult = searchResult, searchStatus = searchStatus, context = context)
            }
        }
    }
}

@Composable
fun ListItem(title: String, artist: String, thumbnail_url: String, onCLick: () -> Unit){
    Row(verticalAlignment = Alignment.Top,
        modifier = Modifier
            .clickable { onCLick() }
            .padding(5.dp)
    ) {
        AsyncImage(model = thumbnail_url,
            contentDescription = title,
            modifier = Modifier
                .width(100.dp)
                .aspectRatio(1f / 1f))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = artist, style = MaterialTheme.typography.titleSmall)
        }
    }
}

//
@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val navController = rememberNavController()
    val searchStatus: SearchStatus = viewModel()
    val searchResult: SearchResult = viewModel()
    val apiStatus: ApiStatus = viewModel()
    SearchScreen(searchResult, searchStatus, apiStatus, navController)
}