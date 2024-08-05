package com.yanfiq.streamfusion.presentation.screens.search

import android.health.connect.datatypes.units.Length
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.dataStore
import com.yanfiq.streamfusion.domain.model.Track
import com.yanfiq.streamfusion.presentation.screens.settings.PreferencesKeys
import com.yanfiq.streamfusion.presentation.ui.theme.AppTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(audiusSearchData: SearchData,
                 soundcloudsearchData: SearchData,
                 spotifySearchData: SearchData,
                 youtubeSearchData: SearchData,
                 navController: NavController,
                 onSearchClick: (searchQuery: String) -> Unit,
                 onPlayClick: (track: Track, platform: StreamingPlatform) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchInput by remember { mutableStateOf("") }

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
                                    onSearchClick(searchQuery)
                                }
                            ) {
                                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Icon")
                                Text(text = "Search")
                            }

                            SearchTabLayout(
                                audiusSearchData = audiusSearchData,
                                soundcloudSearchData = soundcloudsearchData,
                                spotifySearchData = spotifySearchData,
                                youtubeSearchData = youtubeSearchData,
                                onPlayClick = {track, platform ->
                                    onPlayClick(track, platform)
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchTabLayout(audiusSearchData: SearchData,
                    soundcloudSearchData: SearchData,
                    spotifySearchData: SearchData,
                    youtubeSearchData: SearchData,
                    onPlayClick: (track: Track, platform: StreamingPlatform) -> Unit
) {
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
                0 -> SearchResultScreen(isLoading = audiusSearchData.isLoading, message = audiusSearchData.message, result = audiusSearchData.result){Track -> onPlayClick(Track, StreamingPlatform.AUDIUS)}
                1 -> SearchResultScreen(isLoading = soundcloudSearchData.isLoading, message = soundcloudSearchData.message, result = soundcloudSearchData.result){Track -> onPlayClick(Track, StreamingPlatform.SOUNDCLOUD)}
                2 -> SearchResultScreen(isLoading = spotifySearchData.isLoading, message = spotifySearchData.message, result = spotifySearchData.result){Track -> onPlayClick(Track, StreamingPlatform.SPOTIFY)}
                3 -> SearchResultScreen(isLoading = youtubeSearchData.isLoading, message = youtubeSearchData.message, result = youtubeSearchData.result){Track -> onPlayClick(Track, StreamingPlatform.YOUTUBE)}
            }
        }
    }
}

@Composable
fun SearchResultScreen(isLoading: Boolean, message: String, result: List<Track>, onPlayClick: (Track) -> Unit){
    Box(
        modifier = Modifier
            .width((LocalConfiguration.current.screenWidthDp).dp)
            .height((LocalConfiguration.current.screenHeightDp).dp),
    ) {
        if (isLoading){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .height(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Text(text = message, style = MaterialTheme.typography.bodyLarge)
            }
        }else{
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(result) { item ->
                    ListItem(title = item.tractTitle, artist = item.trackArtist, durationInSeconds = item.durationInSeconds, thumbnail_url = item.trackArtworkUrl) {
                        onPlayClick(item)
                    }
                }
            }
        }
    }
}

@Composable
fun ListItem(title: String, artist: String, durationInSeconds: Int, thumbnail_url: String, onCLick: () -> Unit){
    Row(verticalAlignment = Alignment.Top,
        modifier = Modifier
            .clickable { onCLick() }
            .padding(5.dp)
    ) {
        AsyncImage(model = thumbnail_url,
            contentDescription = title,
            placeholder = painterResource(id = R.drawable.music_placeholder),
            modifier = Modifier
                .width(100.dp)
                .aspectRatio(1f / 1f))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = artist, style = MaterialTheme.typography.titleMedium)

            val minutes = (durationInSeconds/60.0f).toInt()
            val seconds = durationInSeconds - (minutes * 60)
            val durationString = "${minutes}:${String.format("%02d", seconds)}"
            Text(text = durationString, style = MaterialTheme.typography.titleMedium)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SearchScreenPreview_loaded() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val tracks = listOf(
        Track("", "Test1", "Test1", 63, ""),
        Track("", "Test2", "Test2", 100, ""),
        Track("", "Test3", "Test3", 100, "")
    )
    val audiusSearchData = SearchData(tracks, false, "")
    val soundcloudsearchData = SearchData(tracks, false, "")
    val spotifySearchData = SearchData(tracks, false, "")
    val youtubeSearchData = SearchData(tracks, false, "")
    SearchScreen(
        audiusSearchData = audiusSearchData,
        soundcloudsearchData = soundcloudsearchData,
        spotifySearchData = spotifySearchData,
        youtubeSearchData = youtubeSearchData,
        navController = navController,
        onSearchClick = { query ->
            Toast.makeText(context, "searching $query", Toast.LENGTH_SHORT).show()
        },
        onPlayClick = {track, platform ->
            Toast.makeText(context, "Playing ${track.tractTitle}", Toast.LENGTH_SHORT).show()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview_loading() {
    val navController = rememberNavController()
    val tracks = emptyList<Track>()
    val audiusSearchData = SearchData(tracks, true, "Fetching Endpoints")
    val soundcloudsearchData = SearchData(tracks, false, "")
    val spotifySearchData = SearchData(tracks, false, "")
    val youtubeSearchData = SearchData(tracks, false, "")
    SearchScreen(
        audiusSearchData = audiusSearchData,
        soundcloudsearchData = soundcloudsearchData,
        spotifySearchData = spotifySearchData,
        youtubeSearchData = youtubeSearchData,
        navController = navController,
        onSearchClick = { query ->

        },
        onPlayClick = {track, platform ->

        }
    )
}