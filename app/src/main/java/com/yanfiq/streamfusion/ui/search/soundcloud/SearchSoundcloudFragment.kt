package com.yanfiq.streamfusion.ui.search.soundcloud

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequestBlocking
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.data.response.soundcloud.Track
import com.yanfiq.streamfusion.ui.search.soundcloud.TrackAdapter

class SearchSoundcloudFragment : Fragment() {
    private lateinit var webView: WebView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var viewOfLayout: View
    private var currentSearch: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        viewOfLayout = inflater.inflate(R.layout.fragment_search_soundcloud, container, false)

        recyclerView = viewOfLayout.findViewById(R.id.recycler_view_soundcloud)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TrackAdapter(emptyList())
        recyclerView.adapter = adapter

//        webView = viewOfLayout.findViewById(R.id.webview_soundcloud)
//        setupWebView()

        return viewOfLayout
    }

//    private fun setupWebView() {
//        webView.settings.javaScriptEnabled = true
//        webView.settings.domStorageEnabled = true
//
//        webView.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//                val url = request?.url.toString()
//                Log.d("isItRedirect", url)
//                if(url.contains("/search?q")){
//                    Log.d("not_redirect", url+" opened in this webview")
//                    return false
//                }
//                val intent = Intent(context, PlaySoundcloudActivity::class.java)
//                intent.putExtra("URL", url)
//                startActivity(intent)
//                Log.d("redirect", "url redirected to another activity")
//                return true
//            }
//
//            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
//                if(!url.toString().contains("/search/sounds?q")) {
//                    val intent = Intent(context, PlaySoundcloudActivity::class.java)
//                    intent.putExtra("URL", url)
//                    startActivity(intent)
//                    Log.d("redirect", "url redirected to another activity")
//                    webView.loadUrl(currentSearch)
//                }
//                else{
//                    super.doUpdateVisitedHistory(view, url, isReload)
//                }
//            }
//
//            override fun onPageFinished(view: WebView?, url: String?) {
//                super.onPageFinished(view, url)
//                injectJavaScript()
//            }
//        }
//        webView.webChromeClient = WebChromeClient()
//    }

    fun searchSoundCloud(query: String) {
        val url = "https://m.soundcloud.com/search/sounds?q=${query.replace(" ", "%20")}&limit=30"
        currentSearch = url
        val doc: Document = Ksoup.parseGetRequestBlocking(url = url)
        val songs_wrapper: Element? = doc.select(".List_VerticalList__2uQYU").first()
        if(songs_wrapper != null){
            val songs: Elements = songs_wrapper.select("li")
            var tracks: MutableList<Track> = mutableListOf()
            songs.forEach{song: Element ->
                var title: String = song.select(".Information_CellTitle__2KitR").html()
                var artist: String = song.select(".Information_CellSubtitle__1mXGx").html()
                var image: String = song.select("img").attr("src")
                var stream_url: String = song.select("a").attr("href");
                var track = Track(title, artist, image, "m.soundcloud.com"+stream_url)
                tracks.add(track)
            }
            adapter = TrackAdapter(tracks)
            recyclerView.adapter = adapter
            adapter.setOnItemClickCallback(object : TrackAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Track) {
                    val intent = Intent(context, PlaySoundcloudActivity::class.java)
                    intent.putExtra("URL", data.stream_url)
                    startActivity(intent)
                }
            })
        }

//        webView.loadUrl(currentSearch)
    }

//    private fun play(track: Track){
//        val explicitIntent = Intent(requireActivity(), PlayAudiusActivity::class.java)
//        explicitIntent.putExtra("TRACK_ID", track.id)
//        explicitIntent.putExtra("TRACK_TITLE", track.title)
//        explicitIntent.putExtra("TRACK_ARTWORK", track.artwork.medium)
//        startActivity(explicitIntent)
//    }
}