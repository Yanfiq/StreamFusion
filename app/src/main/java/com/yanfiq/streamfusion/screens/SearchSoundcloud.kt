package com.yanfiq.streamfusion.screens

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.yanfiq.streamfusion.data.response.soundcloud.Track
import com.yanfiq.streamfusion.data.viewmodel.SearchResult
import com.yanfiq.streamfusion.data.viewmodel.SearchStatus

class JsInterface(private val onResult: (String) -> Unit) {
    @JavascriptInterface
    fun returnPage(value: String) {
        onResult(value)
    }
}

@Composable
fun SoundcloudSearchResult(searchResult: SearchResult = viewModel(), searchStatus: SearchStatus = viewModel(), context: Context, searchQuery: String) {
    val searchResults by searchResult.soundcloudSearchData.observeAsState(initial = emptyList())
    val isSearching by searchStatus.soundcloudSearchStatus.observeAsState(initial = false)
    var url by remember { mutableStateOf("") }
    var result_desired: Int = 30

    if(isSearching){
        url = "https://m.soundcloud.com/search/sounds?q=${searchQuery.replace(" ", "%20")}"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSearching) {
            Log.d("SoundcloudSearch", "isSearching = true")
            Box(
                modifier = Modifier
                    .width((LocalConfiguration.current.screenWidthDp).dp)
                    .height((LocalConfiguration.current.screenHeightDp).dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                AndroidView(
                    modifier = Modifier.align(Alignment.Center),
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    evaluateJavascript(
                                        """
                                    (function(){
                                        let result_desired = ${result_desired};
                                        let result_acquired = 0;
                                    
                                        let interval = setInterval(
                                            function (){
                                                let list = document.querySelector('.List_VerticalList__2uQYU');
                                                result_acquired = list.getElementsByTagName('li').length
                                                console.log(result_acquired);
                                                if(result_acquired >= result_desired){
                                                    Android.returnPage(document.documentElement.outerHTML);
                                                    clearInterval(interval);
                                                }
                                                window.scrollTo(0, document.body.scrollHeight); 
                                                
                                            },
                                            3000
                                        )
                                    })();
                                    """.trimIndent()
                                    ) {  }
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
                                    searchResult.updateSoundcloudSearchData(tracks)
                                }
                                searchStatus.updateSoundcloudSearchStatus(false)
                                Handler(Looper.getMainLooper()).post {
                                    destroy()
                                }
                            }, "Android")
                            loadUrl(url)
                            visibility = View.INVISIBLE
                        }
                    })
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchResults) { item ->
                ListItem(item.title, item.user, item.artwork_url ?: "") {
                    val explicitIntent = Intent(
                        context,
                        com.yanfiq.streamfusion.screens.PlaySoundcloudActivity::class.java
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
