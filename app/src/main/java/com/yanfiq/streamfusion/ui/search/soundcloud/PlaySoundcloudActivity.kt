package com.yanfiq.streamfusion.ui.search.soundcloud

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.ui.youtube.VideoAdapter

class PlaySoundcloudActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var url_played: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_play_soundcloud)

        webView = findViewById(R.id.webview_soundcloud_player)
        setupWebView()

        url_played = intent.getStringExtra("URL")
        if (url_played != null) {
            webView.loadUrl(url_played!!)
        }
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return true
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                injectJavaScript()
            }
            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                if (url != null) {
                    if(url_played?.let { url.contains(it) } == false) {
                        webView.loadUrl(url_played!!)
                        Log.d("webviewproblem", url+" canceled")
                    } else{
                        super.doUpdateVisitedHistory(view, url, isReload)
                    }
                }
            }
        }
        webView.webChromeClient = WebChromeClient()
    }

    private fun injectJavaScript() {
        val js = """
            (function() {
                    const searchBarWeb = document.getElementsByClassName('Header_HeaderPlaceholder__3M8hV');
                    while(searchBarWeb.length > 0){
                        searchBarWeb[0].parentNode.removeChild(searchBarWeb[0]);
                    }
                    const bottomNavigationweb = document.getElementsByClassName('LayoutWrapper_AppDock__v0yuU');
                    while(bottomNavigationweb.length > 0){
                        bottomNavigationweb[0].parentNode.removeChild(bottomNavigationweb[0]);
                    }
                    document.querySelector('footer').remove();
            })();
        """
        webView.evaluateJavascript(js, null)
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}