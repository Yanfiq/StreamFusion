package com.yanfiq.streamfusion.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.ImageLoader
import coil.compose.AsyncImage
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.ui.theme.AppTheme
import com.yanfiq.streamfusion.ui.youtube.VideoAdapter
import kotlinx.coroutines.delay

class PlaySoundcloudActivity : AppCompatActivity() {

    private var url_played: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            SoundcloudPlayScreen(
                trackUrl = "https://w.soundcloud.com/player/?url="+intent.getStringExtra("TRACK_URL"),
                trackTitle = intent.getStringExtra("TRACK_TITLE")!!,
                trackArtist = intent.getStringExtra("TRACK_ARTIST")!!,
                trackArtwork = intent.getStringExtra("TRACK_ARTWORK")!!,
                context = this@PlaySoundcloudActivity
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundcloudPlayScreen(
    trackUrl: String,
    trackTitle: String,
    trackArtist: String,
    trackArtwork: String,
    context: Context
) {
    var isPaused by remember { mutableStateOf(true) }
    var sliderPosition by remember { mutableStateOf(0f) }
    var maxDuration by remember { mutableStateOf(1f) }
    var isPlayerReady by remember { mutableStateOf(false) }
    var webView by remember { mutableStateOf<WebView?>(null) }

    var htmlData = """<!doctypehtml><script src=http://w.soundcloud.com/player/api.js></script><iframe frameborder=no height=120 id=soundcloud_widget src="${trackUrl}&show_artwork=false&liking=false&sharing=false"width=420></iframe><button>Play / Pause</button><script>var SC="object"==typeof SC?SC:{};SC.Widget=function(n){var r={};function o(e){if(r[e])return r[e].exports;var t=r[e]={i:e,l:!1,exports:{}};return n[e].call(t.exports,t,t.exports,o),t.l=!0,t.exports}return o.m=n,o.c=r,o.d=function(e,t,n){o.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:n})},o.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},o.t=function(t,e){if(1&e&&(t=o(t)),8&e||4&e&&"object"==typeof t&&t&&t.__esModule)return t;var n=Object.create(null);if(o.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:t}),2&e&&"string"!=typeof t)for(var r in t)o.d(n,r,function(e){return t[e]}.bind(null,r));return n},o.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return o.d(t,"a",t),t},o.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},o.p="",o(o.s=0)}([function(e,t,n){var c,s,r,o=n(1),u=n(2),i=n(3),a=o.api,l=o.bridge,d=[],f=[],p=/^http(?:s?)/;function E(e){var t,n;for(t=0,n=f.length;t<n&&!1!==e(f[t]);t++);}function g(e){return e.contentWindow?e.contentWindow:e.contentDocument&&"parentWindow"in e.contentDocument?e.contentDocument.parentWindow:null}function v(e){var t,n=[];for(t in e)e.hasOwnProperty(t)&&n.push(e[t]);return n}function S(e,t,n){n.callbacks[e]=n.callbacks[e]||[],n.callbacks[e].push(t)}function _(t,e){var n=!0;return e.callbacks[t]=[],E(function(e){if((e.callbacks[t]||[]).length)return n=!1}),n}function b(e,t,n){var r,o,i=g(n);if(!i.postMessage)return!1;r=n.getAttribute("src").split("?")[0],o=JSON.stringify({method:e,value:t}),"//"===r.substr(0,2)&&(r=window.location.protocol+r),r=r.replace(/http:\/\/(w|wt).soundcloud.com/,"https://${'$'}1.soundcloud.com"),i.postMessage(o,r)}function y(t){var n;return E(function(e){if(e.instance===t)return n=e,!1}),n}function h(t){var n;return E(function(e){if(g(e.element)===t)return n=e,!1}),n}function m(i,u){return function(e){var t,n=!!((t=e)&&t.constructor&&t.call&&t.apply),r=y(this),o=n&&!u?e:null;return o&&S(i,o,r),b(i,!n&&u?e:null,r.element),this}}function R(e,t,n){var r,o,i;for(r=0,o=t.length;r<o;r++)e[i=t[r]]=m(i,n)}function w(e,t,n){return e+"?url="+t+"&"+function(e){var t,n,r=[];for(t in e)e.hasOwnProperty(t)&&(n=e[t],r.push(t+"="+("start_track"===t?parseInt(n,10):n?"true":"false")));return r.join("&")}(n)}function O(e,t,n){var r,o,i=e.callbacks[t]||[];for(r=0,o=i.length;r<o;r++)i[r].apply(e.instance,n);(function(e){var t,n=!1;for(t in u)if(u.hasOwnProperty(t)&&u[t]===e){n=!0;break}return n}(t)||t===a.READY)&&(e.callbacks[t]=[])}function A(e){var t,n,r,o,i;try{n=JSON.parse(e.data)}catch(e){return!1}return t=h(e.source),r=n.method,o=n.value,(!t||P(e.origin)===P(t.domain))&&(t?(r===a.READY&&(t.isReady=!0,O(t,"__LATE_BINDING__"),_("__LATE_BINDING__",t)),r!==a.PLAY||t.playEventFired||(t.playEventFired=!0),r!==a.PLAY_PROGRESS||t.playEventFired||(t.playEventFired=!0,O(t,a.PLAY,[o])),i=[],void 0!==o&&i.push(o),void O(t,r,i)):(r===a.READY&&d.push(e.source),!1))}function P(e){return e.replace(p,"")}window.addEventListener?window.addEventListener("message",A,!1):window.attachEvent("onmessage",A),e.exports=r=function(e,t,n){if((""===(o=e)||o&&o.charCodeAt&&o.substr)&&(e=document.getElementById(e)),!(r=e)||1!==r.nodeType||"IFRAME"!==r.nodeName.toUpperCase())throw Error("SC.Widget function should be given either iframe element or a string specifying id attribute of iframe element.");t&&(n=n||{},e.src=w("http://wt.soundcloud.test:9200/",t,n));var r,o,i,u,a=h(g(e));return a&&a.instance?a.instance:(i=-1<d.indexOf(g(e)),u=new c(e),f.push(new s(u,e,i)),u)},r.Events=a,window.SC=window.SC||{},window.SC.Widget=r,s=function(e,t,n){this.instance=e,this.element=t,this.domain=function(e){var t,n,r,o="";for("//"===e.substr(0,2)&&(e=window.location.protocol+e),t=0,n=(r=e.split("/")).length;t<n&&t<3;t++)o+=r[t],t<2&&(o+="/");return o}(t.getAttribute("src")),this.isReady=!!n,this.callbacks={}},(c=function(){}).prototype={constructor:c,load:function(e,n){if(e){n=n||{};var t=this,r=y(this),o=r.element,i=o.src,u=i.substr(0,i.indexOf("?"));r.isReady=!1,r.playEventFired=!1,o.onload=function(){t.bind(a.READY,function(){var e,t=r.callbacks;for(e in t)t.hasOwnProperty(e)&&e!==a.READY&&b(l.ADD_LISTENER,e,r.element);n.callback&&n.callback()})},o.src=w(u,e,n)}},bind:function(e,t){var n=this,r=y(this);return r&&r.element&&(e===a.READY&&r.isReady?setTimeout(t,1):r.isReady?(S(e,t,r),b(l.ADD_LISTENER,e,r.element)):S("__LATE_BINDING__",function(){n.bind(e,t)},r)),this},unbind:function(e){var t,n=y(this);n&&n.element&&(t=_(e,n),e!==a.READY&&t&&b(l.REMOVE_LISTENER,e,n.element))}},R(c.prototype,v(u)),R(c.prototype,v(i),!0)},function(e,t){t.api={LOAD_PROGRESS:"loadProgress",PLAY_PROGRESS:"playProgress",PLAY:"play",PAUSE:"pause",FINISH:"finish",SEEK:"seek",READY:"ready",OPEN_SHARE_PANEL:"sharePanelOpened",CLICK_DOWNLOAD:"downloadClicked",CLICK_BUY:"buyClicked",ERROR:"error"},t.bridge={REMOVE_LISTENER:"removeEventListener",ADD_LISTENER:"addEventListener"}},function(e,t){e.exports={GET_VOLUME:"getVolume",GET_DURATION:"getDuration",GET_POSITION:"getPosition",GET_SOUNDS:"getSounds",GET_CURRENT_SOUND:"getCurrentSound",GET_CURRENT_SOUND_INDEX:"getCurrentSoundIndex",IS_PAUSED:"isPaused"}},function(e,t){e.exports={PLAY:"play",PAUSE:"pause",TOGGLE:"toggle",SEEK_TO:"seekTo",SET_VOLUME:"setVolume",NEXT:"next",PREV:"prev",SKIP:"skip"}}]);var widget=SC.Widget(document.getElementById("soundcloud_widget"));widget.bind(SC.Widget.Events.READY,function(){widget.getDuration(function(e){AndroidInterface.returnDuration(e/1e3)})}),widget.bind(SC.Widget.Events.PLAY_PROGRESS,function(){widget.getPosition(function(e){AndroidInterface.returnElapsedTime(e/1e3)})}),document.querySelector("button").addEventListener("click",function(){console.log("Button pressed"),widget.toggle()})</script>"""

    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
        }
    }

    AppTheme {
        Surface (
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WebViewScreen_HtmlString(htmlData = htmlData, returnWebView = { webView_returned ->
                    webView = webView_returned
                }, returnDuration = { returnDuration ->
                    maxDuration = returnDuration
                    isPlayerReady = true
                }, returnElapsedTime = { elapsedTime ->
                    sliderPosition = elapsedTime
                })

                Text(
                    text = trackTitle,
                    style = MaterialTheme.typography.titleLarge)
                Text(
                    text = trackArtist,
                    style = MaterialTheme.typography.titleMedium)
                AsyncImage(
                    model = trackArtwork,
                    contentDescription = trackTitle,
                    imageLoader = ImageLoader(context),
                    modifier = Modifier
                        .width(400.dp)
                        .height(400.dp))
                Slider(
                    value = sliderPosition,
                    onValueChange = { newValue ->
                        sliderPosition = newValue
                        webView?.evaluateJavascript("SC.Widget(document.getElementById('soundcloud_widget')).seekTo(${sliderPosition * 1000f})", null)
                    },
                    valueRange = 0f..maxDuration,
                    enabled = isPlayerReady,
                    modifier = Modifier.fillMaxWidth()) // Slider functionality
                Button(
                    onClick = {
                        webView?.evaluateJavascript("SC.Widget(document.getElementById('soundcloud_widget')).toggle()", null)
                        isPaused = !isPaused
                    },
                    enabled = isPlayerReady,
                    modifier = Modifier
                        .width(75.dp)
                        .height(75.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = "Play/Pause button",
                        modifier = Modifier.fillMaxSize(0.60f)
                    )
                }
            }
        }
    }
}

class WebAppInterface(private val context: Context, private val onDurationReceived: (Float) -> Unit, private val onElapsedTimeReceived: (Float) -> Unit) {

    @JavascriptInterface
    fun returnDuration(duration: Float) {
        onDurationReceived(duration)
    }

    @JavascriptInterface
    fun returnElapsedTime(elapsedTime: Float) {
        onElapsedTimeReceived(elapsedTime)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen_HtmlString(
    htmlData: String,
    returnWebView: (WebView) -> Unit,
    returnDuration: (Float) -> Unit,
    returnElapsedTime: (Float) -> Unit
) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.userAgentString =
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36" //desktop mode to prevent "Open on app"
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            addJavascriptInterface(
                WebAppInterface(context, returnDuration, returnElapsedTime),
                "AndroidInterface"
            )
            visibility = View.GONE
            loadData(htmlData, "text/html", "UTF-8")
            returnWebView(this)
        }
    })
}

@Preview
@Composable
private fun Preview_soundcloud_play(){
    val context = LocalContext.current
    SoundcloudPlayScreen(
        trackUrl = "Acumalaka",
        trackTitle = "Dear You - DJ Genericname",
        trackArtist = "DJ Genericname",
        trackArtwork = "https://i1.sndcdn.com/artworks-000057356357-9tmqex-t500x500.jpg",
        context = context
    )
}