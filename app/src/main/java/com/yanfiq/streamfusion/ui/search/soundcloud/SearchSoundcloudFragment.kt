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
import com.yanfiq.streamfusion.R

class SearchSoundcloudFragment : Fragment() {
    private lateinit var webView: WebView
    private lateinit var viewOfLayout: View
    private var currentSearch: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout = inflater.inflate(R.layout.fragment_search_soundcloud, container, false)

        webView = viewOfLayout.findViewById(R.id.webview_soundcloud)
        setupWebView()

        return viewOfLayout
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                Log.d("isItRedirect", url)
                if(url.contains("/search?q")){
                    Log.d("not_redirect", url+" opened in this webview")
                    return false
                }
                val intent = Intent(context, PlaySoundcloudActivity::class.java)
                intent.putExtra("URL", url)
                startActivity(intent)
                Log.d("redirect", "url redirected to another activity")
                return true
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                if(!url.toString().contains("/search/sounds?q")) {
                    val intent = Intent(context, PlaySoundcloudActivity::class.java)
                    intent.putExtra("URL", url)
                    startActivity(intent)
                    Log.d("redirect", "url redirected to another activity")
                    webView.loadUrl(currentSearch)
                }
                else{
                    super.doUpdateVisitedHistory(view, url, isReload)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                injectJavaScript()
            }
        }
        webView.webChromeClient = WebChromeClient()
    }

    fun searchSoundCloud(query: String) {
        val url = "https://m.soundcloud.com/search/sounds?q=${query.replace(" ", "%20")}"
        currentSearch = url
        webView.loadUrl(currentSearch)
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
                    const tabSearchWeb = document.getElementsByClassName('Tabs_TabNavList__6GhOq');
                    while(tabSearchWeb.length > 0){
                        tabSearchWeb[0].parentNode.removeChild(tabSearchWeb[0]);
                    }
            })();
        """
        webView.evaluateJavascript(js, null)
    }
}