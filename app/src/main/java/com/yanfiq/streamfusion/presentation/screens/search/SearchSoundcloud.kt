package com.yanfiq.streamfusion.presentation.screens.search

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.yanfiq.streamfusion.domain.model.soundcloud.Track
import com.yanfiq.streamfusion.data.viewmodel.SearchResult
import com.yanfiq.streamfusion.data.viewmodel.SearchStatus
import com.yanfiq.streamfusion.dataStore
import com.yanfiq.streamfusion.presentation.screens.settings.PreferencesKeys
import com.yanfiq.streamfusion.presentation.screens.player.PlaySoundcloudActivity
import kotlinx.coroutines.flow.map

class JsInterface(private val onResult: (String) -> Unit) {
    @JavascriptInterface
    fun returnPage(value: String) {
        onResult(value)
    }
}

fun searchSoundcloud(
    context: Context,
    query: String,
    limit: Int,
    onResponse: (List<Track>) -> Unit
) {
    val url = "https://m.soundcloud.com/search/sounds?q=${query.replace(" ", "%20")}"

    // Create a FrameLayout to hold the WebView
    val frameLayout = FrameLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(1, 1) // Smallest size possible to be hidden
    }

    val webView = WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(1200, 3000)
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                evaluateJavascript(
                    """
                        (function(){
                            let result_desired = ${limit};
                            let result_acquired = 0;
                        
                            let interval = setInterval(
                                function (){
                                    let list = document.querySelector('.List_VerticalList__2uQYU');
                                    let showingAllItem = document.querySelector('.LazyLoadingList_InlineLoadingMessage__2DOT2');
                                    result_acquired = list.getElementsByTagName('li').length;
                                    console.log(result_acquired+' | '+showingAllItem.innerText);
                                    if(result_acquired >= result_desired || showingAllItem.innerText.includes('Showing all tracks')){
                                        Android.returnPage(document.documentElement.outerHTML);
                                        clearInterval(interval);
                                    }
                                    window.scrollTo(0, document.body.scrollHeight); 
                                },
                                3000
                            )
                        })();
                        """.trimIndent()
                ) { }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
            }
        }
        addJavascriptInterface(JsInterface { value ->
            val doc: Document = Ksoup.parse(value)
            val songsWrapper: Element? = doc.select(".List_VerticalList__2uQYU").first()
            if (songsWrapper != null) {
                val songs: Elements = songsWrapper.select("li")
                val tracks: MutableList<Track> = mutableListOf()
                songs.forEach { song: Element ->
                    val title: String = song.select(".Information_CellTitle__2KitR").html()
                    val artist: String = song.select(".Information_CellSubtitle__1mXGx").html()
                    val image: String = song.select("img").attr("src")
                    val streamUrl: String = song.select("a").attr("href")
                    val track = Track(title, artist, image, "https://soundcloud.com$streamUrl")
                    tracks.add(track)
                    Log.d("SoundcloudSearch", title)
                }
                onResponse(tracks)
            }
            Handler(Looper.getMainLooper()).post {
                val rootView = (context as Activity).findViewById<ViewGroup>(R.id.content)
                rootView.removeView(frameLayout)
                destroy()
            }
        }, "Android")
        loadUrl(url)
        visibility = View.INVISIBLE
        isClickable = false
        isFocusable = false
    }

    // Add the WebView to the FrameLayout
    frameLayout.addView(webView)

    // Create a root view (e.g., a LinearLayout) to attach the FrameLayout to the activity's content view
    val rootView = (context as Activity).findViewById<ViewGroup>(android.R.id.content)
    rootView.addView(frameLayout)
}

@Composable
fun SoundcloudSearchResult(searchResult: SearchResult, searchStatus: SearchStatus, context: Context, searchQuery: String) {
    val searchResults by searchResult.soundcloudSearchData.observeAsState(initial = emptyList())
    val isSearching by searchStatus.soundcloudSearchStatus.observeAsState(initial = false)
    val limit by (LocalContext.current.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.RESULT_PER_SEARCH] ?: 10f
    }).collectAsState(initial = 10f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSearching) {
            Box(
                modifier = Modifier
                    .width((LocalConfiguration.current.screenWidthDp).dp)
                    .height((LocalConfiguration.current.screenHeightDp).dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchResults) { item ->
                ListItem(item.title, item.user, item.artwork_url ?: "") {
                    val explicitIntent = Intent(
                        context,
                        PlaySoundcloudActivity::class.java
                    )
                    explicitIntent.putExtra("TRACK_TITLE", item.title)
                    explicitIntent.putExtra("TRACK_ARTIST", item.user)
                    explicitIntent.putExtra("TRACK_ARTWORK", item.artwork_url)
                    explicitIntent.putExtra("TRACK_URL", item.stream_url)
                    startActivity(context, explicitIntent, null)
                }
            }
        }
    }
}
