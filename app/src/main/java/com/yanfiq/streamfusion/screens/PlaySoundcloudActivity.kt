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

    var htmlData = """
    <!doctype html>
    <html>
    <head>
      <script type="text/javascript" src="http://w.soundcloud.com/player/api.js"></script>
    </head>
    <body>
      <iframe id="soundcloud_widget"
          src="${trackUrl}&show_artwork=false&liking=false&sharing=false"
          width="420"
          height="120"
          frameborder="no"></iframe>
      <button>Play / Pause</button>
    
      <script>
        var SC="object"==typeof SC?SC:{};SC.Widget=function(e){var t={};function n(r){if(t[r])return t[r].exports;var o=t[r]={i:r,l:!1,exports:{}};return e[r].call(o.exports,o,o.exports,n),o.l=!0,o.exports}return n.m=e,n.c=t,n.d=function(e,t,r){n.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:r})},n.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},n.t=function(e,t){if(1&t&&(e=n(e)),8&t||4&t&&"object"==typeof e&&e&&e.__esModule)return e;var r=Object.create(null);if(n.r(r),Object.defineProperty(r,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var o in e)n.d(r,o,(function(t){return e[t]}).bind(null,o));return r},n.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return n.d(t,"a",t),t},n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},n.p="",n(n.s=0)}([function(e,t,n){var r,o,i,u=n(1),a=n(2),s=n(3),c=u.api,l=u.bridge,f=[],d=[],p=/^http(?:s?)/;function E(e){var t,n;for(t=0,n=d.length;t<n&&!1!==e(d[t]);t++);}function v(e){return e.contentWindow?e.contentWindow:e.contentDocument&&"parentWindow"in e.contentDocument?e.contentDocument.parentWindow:null}function h(e){var t,n=[];for(t in e)e.hasOwnProperty(t)&&n.push(e[t]);return n}function y(e,t,n){n.callbacks[e]=n.callbacks[e]||[],n.callbacks[e].push(t)}function S(e,t){var n=!0;return t.callbacks[e]=[],E(function(t){if((t.callbacks[e]||[]).length)return n=!1,!1}),n}function g(e,t,n){var r,o,i=v(n);if(!i.postMessage)return!1;r=n.getAttribute("src").split("?")[0],o=JSON.stringify({method:e,value:t}),"//"===r.substr(0,2)&&(r=window.location.protocol+r),r=r.replace(/http:\/\/(w|wt).soundcloud.com/,"https://${'$'}1.soundcloud.com"),i.postMessage(o,r)}function R(e){var t;return E(function(n){if(n.instance===e)return t=n,!1}),t}function b(e){var t;return E(function(n){if(v(n.element)===e)return t=n,!1}),t}function m(e,t){return function(n){var r,o=!!((r=n)&&r.constructor&&r.call&&r.apply),i=R(this),u=o&&!t?n:null;return u&&y(e,u,i),g(e,!o&&t?n:null,i.element),this}}function A(e,t,n){var r,o,i;for(r=0,o=t.length;r<o;r++)e[i=t[r]]=m(i,n)}function P(e,t,n){return e+"?url="+t+"&"+function(e){var t,n,r=[];for(t in e)e.hasOwnProperty(t)&&(n=e[t],r.push(t+"="+("start_track"===t?parseInt(n,10):n?"true":"false")));return r.join("&")}(n)}function D(e,t,n){var r,o,i=e.callbacks[t]||[];for(r=0,o=i.length;r<o;r++)i[r].apply(e.instance,n);((function(e){var t,n=!1;for(t in a)if(a.hasOwnProperty(t)&&a[t]===e){n=!0;break}return n})(t)||t===c.READY)&&(e.callbacks[t]=[])}function O(e){var t,n,r,o,i;try{n=JSON.parse(e.data)}catch(u){return!1}return t=b(e.source),r=n.method,o=n.value,(!t||L(e.origin)===L(t.domain))&&(t?(r===c.READY&&(t.isReady=!0,D(t,"__LATE_BINDING__"),S("__LATE_BINDING__",t)),r!==c.PLAY||t.playEventFired||(t.playEventFired=!0),r!==c.PLAY_PROGRESS||t.playEventFired||(t.playEventFired=!0,D(t,c.PLAY,[o])),i=[],void 0!==o&&i.push(o),void D(t,r,i)):(r===c.READY&&f.push(e.source),!1))}function L(e){return e.replace(p,"")}window.addEventListener?window.addEventListener("message",O,!1):window.attachEvent("onmessage",O),e.exports=i=function(e,t,n){if((""===(u=e)||u&&u.charCodeAt&&u.substr)&&(e=document.getElementById(e)),!(i=e)||1!==i.nodeType||"IFRAME"!==i.nodeName.toUpperCase())throw Error("SC.Widget function should be given either iframe element or a string specifying id attribute of iframe element.");t&&(n=n||{},e.src=P("http://wt.soundcloud.test:9200/",t,n));var i,u,a,s,c=b(v(e));return c&&c.instance?c.instance:(a=f.indexOf(v(e))>-1,s=new r(e),d.push(new o(s,e,a)),s)},i.Events=c,window.SC=window.SC||{},window.SC.Widget=i,o=function(e,t,n){this.instance=e,this.element=t,this.domain=function(e){var t,n,r,o="";for("//"===e.substr(0,2)&&(e=window.location.protocol+e),r=e.split("/"),t=0,n=r.length;t<n&&t<3;t++)o+=r[t],t<2&&(o+="/");return o}(t.getAttribute("src")),this.isReady=!!n,this.callbacks={}},(r=function(){}).prototype={constructor:r,load:function(e,t){if(e){t=t||{};var n=this,r=R(this),o=r.element,i=o.src,u=i.substr(0,i.indexOf("?"));r.isReady=!1,r.playEventFired=!1,o.onload=function(){n.bind(c.READY,function(){var e,n=r.callbacks;for(e in n)n.hasOwnProperty(e)&&e!==c.READY&&g(l.ADD_LISTENER,e,r.element);t.callback&&t.callback()})},o.src=P(u,e,t)}},bind:function(e,t){var n=this,r=R(this);return r&&r.element&&(e===c.READY&&r.isReady?setTimeout(t,1):r.isReady?(y(e,t,r),g(l.ADD_LISTENER,e,r.element)):y("__LATE_BINDING__",function(){n.bind(e,t)},r)),this},unbind:function(e){var t,n=R(this);n&&n.element&&(t=S(e,n),e!==c.READY&&t&&g(l.REMOVE_LISTENER,e,n.element))}},A(r.prototype,h(a)),A(r.prototype,h(s),!0)},function(e,t){t.api={LOAD_PROGRESS:"loadProgress",PLAY_PROGRESS:"playProgress",PLAY:"play",PAUSE:"pause",FINISH:"finish",SEEK:"seek",READY:"ready",OPEN_SHARE_PANEL:"sharePanelOpened",CLICK_DOWNLOAD:"downloadClicked",CLICK_BUY:"buyClicked",ERROR:"error"},t.bridge={REMOVE_LISTENER:"removeEventListener",ADD_LISTENER:"addEventListener"}},function(e,t){e.exports={GET_VOLUME:"getVolume",GET_DURATION:"getDuration",GET_POSITION:"getPosition",GET_SOUNDS:"getSounds",GET_CURRENT_SOUND:"getCurrentSound",GET_CURRENT_SOUND_INDEX:"getCurrentSoundIndex",IS_PAUSED:"isPaused"}},function(e,t){e.exports={PLAY:"play",PAUSE:"pause",TOGGLE:"toggle",SEEK_TO:"seekTo",SET_VOLUME:"setVolume",NEXT:"next",PREV:"prev",SKIP:"skip"}}]);
        var widget = SC.Widget(document.getElementById('soundcloud_widget'));
        widget.bind(SC.Widget.Events.READY, function() {
          widget.getDuration(function(duration){
             AndroidInterface.returnDuration(duration / 1000);
          });
        });
    
        widget.bind(SC.Widget.Events.PLAY_PROGRESS, function(){
            widget.getPosition(function(position){
                AndroidInterface.returnElapsedTime(position / 1000);
            })
        })
    
        document.querySelector('button').addEventListener("click", function() {
          console.log("Button pressed");
          widget.toggle();
        });
       </script>
    </body>
    </html>
    """.trimIndent()

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
                WebViewScreen(htmlData = htmlData, returnWebView = { webView_returned ->
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
fun WebViewScreen(
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
fun preview_soundcloud_play(){
    val context = LocalContext.current
    SoundcloudPlayScreen(
        trackUrl = "Acumalaka",
        trackTitle = "Dear You - DJ Genericname",
        trackArtist = "DJ Genericname",
        trackArtwork = "https://i1.sndcdn.com/artworks-000057356357-9tmqex-t500x500.jpg",
        context = context
    )
}