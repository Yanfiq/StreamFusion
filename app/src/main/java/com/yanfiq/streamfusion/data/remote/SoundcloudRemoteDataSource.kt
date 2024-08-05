package com.yanfiq.streamfusion.data.remote

import android.R
import android.app.Activity
import android.content.Context
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
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.yanfiq.streamfusion.data.repositories.SoundcloudRepository
import com.yanfiq.streamfusion.domain.model.Track

class JsInterface(private val onResult: (String) -> Unit) {
    @JavascriptInterface
    fun returnPage(value: String) {
        onResult(value)
    }
}

class SoundcloudRemoteDataSource: SoundcloudRepository {
    override suspend fun search(
        query: String,
        limit: Int,
        context: Context,
        onResults: (List<Track>) -> Unit
    ) {
        val url = "https://m.soundcloud.com/search/sounds?q=${query.replace(" ", "%20")}"

        // Create a FrameLayout to hold the WebView
        val frameLayout = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(1, 1) // Smallest size possible to be hidden
        }

        val webView = WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(1200, 2000)
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
                Log.d("SoundcloudSearch", "Javascript interface called")
                (context as Activity).runOnUiThread {
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
                            val durationRaw: String = song.select(".Metadata_MetadataLabel__3GU8Y")[1].html()
                            val duration: Int = (durationRaw.substring(0, durationRaw.indexOf(':')).toInt() * 60) + (durationRaw.substring(durationRaw.indexOf(':')+1).toInt())
                            val track = Track("https://soundcloud.com$streamUrl", title, artist, duration, image)
                            tracks.add(track)
                            Log.d("SoundcloudSearch", title)
                        }
                        onResults(tracks)
                    }
                    Handler(Looper.getMainLooper()).post {
                        (context as Activity).runOnUiThread {
                            val rootView = (context as Activity).findViewById<ViewGroup>(R.id.content)
                            rootView.removeView(frameLayout)
                            destroy()
                        }
                    }
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
        (context as Activity).runOnUiThread {
            val rootView = (context as Activity).findViewById<ViewGroup>(android.R.id.content)
            rootView.addView(frameLayout)
        }
    }
}