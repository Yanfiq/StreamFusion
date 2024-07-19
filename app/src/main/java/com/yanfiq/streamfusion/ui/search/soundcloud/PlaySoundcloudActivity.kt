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

        var url_received = intent.getStringExtra("URL")
        if (url_received != null) {
            url_played = "https://w.soundcloud.com/player/?url="+url_received.replace("m.soundcloud", "soundcloud")
            webView.loadUrl(url_played!!)
        }
    }

    private fun setupWebView() {
//        webView.getSettings().userAgentString = "Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13"
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
        }
        webView.webChromeClient = WebChromeClient()
        setDesktopMode(webView, true)
    }

    private fun injectJavaScript() {
        val js = """
            (function() {
                    const overlay = document.getElementsByClassName('mobilePrestitial g-flex-row-centered g-box-full m-enabled');
                    while(overlay.length > 0){
                        overlay[0].parentNode.removeChild(overlay[0]);
                    }
            })();
        """
        webView.evaluateJavascript(js, null)
    }

    private fun setDesktopMode(webView: WebView, enabled: Boolean) {
        var newUserAgent: String? = webView.settings.userAgentString
        if (enabled) {
            newUserAgent = "Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13"
        }
        webView.settings.apply {
            userAgentString = newUserAgent
            useWideViewPort = enabled
            loadWithOverviewMode = enabled
        }
        webView.reload()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}