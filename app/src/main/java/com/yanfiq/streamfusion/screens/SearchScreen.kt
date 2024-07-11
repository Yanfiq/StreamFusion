package com.yanfiq.streamfusion.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import coil.compose.AsyncImage
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.network.parseGetRequestBlocking
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.yanfiq.streamfusion.data.response.audius.AudiusResponse
import com.yanfiq.streamfusion.ui.theme.NavigationBarMediumTheme
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import com.yanfiq.streamfusion.screens.PlayAudiusActivity
import com.yanfiq.streamfusion.ui.search.soundcloud.PlaySoundcloudActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SearchScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    var searchResults_audius by remember { mutableStateOf(emptyList<com.yanfiq.streamfusion.data.response.audius.Track>()) }
    var searchResults_soundcloud by remember { mutableStateOf(emptyList<com.yanfiq.streamfusion.data.response.soundcloud.Track>()) }
    var searchResults_youtube by remember { mutableStateOf(emptyList<com.yanfiq.streamfusion.data.response.youtube.Video>()) }

    NavigationBarMediumTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(text = "Keyword") }
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
//                        search_audius(searchQuery, context) { results ->
//                            searchResults_audius = results
//                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            search_soundcloud(searchQuery, context){ results ->
                                searchResults_soundcloud = results
                            }
                        }
                    }
                ) {
                    Text(text = "Search")
                }

                ParallelSearchTab(context, searchResults_audius, searchResults_soundcloud)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParallelSearchTab(context: Context,
                      searchResults_audius: List<com.yanfiq.streamfusion.data.response.audius.Track>,
                      searchResults_soundcloud: List<com.yanfiq.streamfusion.data.response.soundcloud.Track>) {
    val tabs = listOf("Audius", "SoundCloud", "Spotify", "YouTube")
    var selectedTabIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = {
        4
    })
    val coroutineScope = rememberCoroutineScope()

    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            selectedTabIndex = pagerState.currentPage
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                        },
                    text = { Text(text = title, style = MaterialTheme.typography.titleSmall) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> AudiusSearchResult(context, searchResults_audius)
                1 -> SoundcloudSearchResult(context, searchResults_soundcloud)
                2 -> SpotifySearchResult()
                3 -> YoutubeSearchResult()
            }
        }
    }
}

fun search_audius(query: String, context: Context, onResults: (List<com.yanfiq.streamfusion.data.response.audius.Track>) -> Unit) {
    // Perform search and call onResults with the search results
    //audius
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val limit = sharedPreferences.getString("result_per_query", "10")!!.toInt()
    if(AudiusEndpointUtil.getUsedEndpoint() != null){
        Log.d("AudiusSearch", AudiusEndpointUtil.getUsedEndpoint().toString())
        val api = AudiusEndpointUtil.getApiInstance()
        api?.searchTracks(query, limit)?.enqueue(object : Callback<AudiusResponse> {
            override fun onResponse(call: Call<AudiusResponse>, response: Response<AudiusResponse>) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.data ?: emptyList()
                    onResults(tracks)
                } else {
                    Log.d("AudiusSearch", "Response not successful: ${response.errorBody()?.string()}")
                    onResults(emptyList())
                }
            }

            override fun onFailure(call: Call<AudiusResponse>, t: Throwable) {
                Log.d("AudiusSearch", "API call failed: ${t.message}")
                onResults(emptyList())
            }
        })
    }

}

suspend fun search_soundcloud(query: String, context: Context, onResults: (List<com.yanfiq.streamfusion.data.response.soundcloud.Track>) -> Unit){
    val url = "https://m.soundcloud.com/search/sounds?q=${query.replace(" ", "%20")}"
    val doc: Document = Ksoup.parseGetRequest(url = url)
    val songs_wrapper: Element? = doc.select(".List_VerticalList__2uQYU").first()
    if(songs_wrapper != null){
        val songs: Elements = songs_wrapper.select("li")
        var tracks: MutableList<com.yanfiq.streamfusion.data.response.soundcloud.Track> = mutableListOf()
        songs.forEach{song: Element ->
            var title: String = song.select(".Information_CellTitle__2KitR").html()
            var artist: String = song.select(".Information_CellSubtitle__1mXGx").html()
            var image: String = song.select("img").attr("src")
            var stream_url: String = song.select("a").attr("href");
            var track = com.yanfiq.streamfusion.data.response.soundcloud.Track(title, artist, image, "soundcloud.com"+stream_url)
            tracks.add(track)
        }
        onResults(tracks)
    }else{
        onResults(emptyList())
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
            modifier = Modifier.width(100.dp)
                .aspectRatio(1f/1f))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = artist, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
fun AudiusSearchResult(context: Context, searchResults_audius: List<com.yanfiq.streamfusion.data.response.audius.Track>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(searchResults_audius) { item ->
            ListItem(item.title, item.user.name, item.artwork.small) {
                val explicitIntent = Intent(context, PlayAudiusActivity::class.java)
                explicitIntent.putExtra("TRACK_ID", item.id)
                explicitIntent.putExtra("TRACK_TITLE", item.title)
                explicitIntent.putExtra("TRACK_ARTIST", item.user.name)
                explicitIntent.putExtra("TRACK_ARTWORK", item.artwork.large)
                startActivity(context, explicitIntent, null)
            }
        }
    }
}

@Composable
fun SoundcloudSearchResult(context: Context, searchResults_soundcloud: List<com.yanfiq.streamfusion.data.response.soundcloud.Track>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(searchResults_soundcloud) { item ->
            ListItem(item.title, item.user, item.artwork_url ?: "") {
                val explicitIntent = Intent(context, com.yanfiq.streamfusion.screens.PlaySoundcloudActivity::class.java)
                explicitIntent.putExtra("TRACK_TITLE", item.title)
                explicitIntent.putExtra("TRACK_ARTIST", item.user)
                explicitIntent.putExtra("TRACK_ARTWORK", item.artwork_url)
                explicitIntent.putExtra("TRACK_URL", item.stream_url)
                startActivity(context, explicitIntent, null)
            }
        }
    }
}

@Composable
fun SpotifySearchResult() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Content for Tab 3")
    }
}

@Composable
fun YoutubeSearchResult() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Content for Tab 4")
    }
}


@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val navController = rememberNavController()
    SearchScreen(navController)
}