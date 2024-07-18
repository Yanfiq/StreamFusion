package com.yanfiq.streamfusion.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import com.yanfiq.streamfusion.data.viewmodel.ApiStatus
import com.yanfiq.streamfusion.data.viewmodel.SearchResult
import com.yanfiq.streamfusion.data.viewmodel.SearchStatus
import com.yanfiq.streamfusion.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(searchResult: SearchResult, searchStatus: SearchStatus, apiStatus: ApiStatus, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var searchInput by remember { mutableStateOf("") }
    val context = LocalContext.current

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
                    TopAppBar(title = {
                        Text(text = "Search")
                    })
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
//                                searchStatus.updateSoundcloudSearchStatus(true)
//                                searchStatus.updateAudiusSearchStatus(true)
                                searchStatus.updateYoutubeSearchStatus(true)
                                searchQuery = searchInput
                            }
                        ) {
                            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Icon")
                            Text(text = "Search")
                        }

                        SearchTabLayout(searchResult, searchStatus, apiStatus, context, searchQuery)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchTabLayout(searchResult: SearchResult, searchStatus: SearchStatus, apiStatus: ApiStatus,context: Context, searchQuery: String) {
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
                0 -> AudiusSearchResult(apiStatus = apiStatus, searchResult = searchResult, searchStatus = searchStatus, context = context, searchQuery = searchQuery)
                1 -> SoundcloudSearchResult(searchResult = searchResult, searchStatus = searchStatus, context = context, searchQuery = searchQuery)
                2 -> SpotifySearchResult()
                3 -> YoutubeSearchResult(searchResult = searchResult, searchStatus = searchStatus, context = context, searchQuery = searchQuery)
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

@Composable
fun SpotifySearchResult() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Content for Tab 3")
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