package com.yanfiq.streamfusion.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.yanfiq.streamfusion.data.response.soundcloud.Track

class JsInterface(private val onResult: (String) -> Unit) {
    @JavascriptInterface
    fun returnPage(value: String) {
        onResult(value)
    }
}

@Composable
fun SoundcloudSearchResult(context: Context, newQuery: String) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<Track>()) }
    var isSearching by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }
    var result_desired: Int = 30

    if(newQuery != searchQuery){
        Log.d("SoundcloudSearch", "old:${searchQuery}|new:${newQuery}")
        searchQuery = newQuery
        url = "https://m.soundcloud.com/search/sounds?q=${searchQuery.replace(" ", "%20")}"
        isSearching = true
    }

    Column {
        if (isSearching) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
//                        layoutParams = ViewGroup.LayoutParams(1, 1)
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
                                searchResults = tracks
                            }
                            isSearching = false
                            destroy()
                        }, "Android")
                        loadUrl(url)
                        visibility = View.INVISIBLE
                    }
                })
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
