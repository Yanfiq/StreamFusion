package com.yanfiq.streamfusion.ui.search.audius

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask

class PlayAudiusActivity : AppCompatActivity() {

    private lateinit var trackid: String
    private lateinit var trackTitle: TextView
    private lateinit var trackArtwork: ImageView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var webViewPlayer: WebView
    private lateinit var seekBar: SeekBar
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var url: String
    private var timer: Timer? = null
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_audius)
//        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        trackTitle = findViewById(R.id.track_title)
        trackArtwork = findViewById(R.id.track_artwork)
        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)
        webViewPlayer = findViewById(R.id.webview_audius_player)
        seekBar = findViewById(R.id.seekBar)

        trackid = intent.getStringExtra("TRACK_ID") ?: ""
        trackTitle.text = intent.getStringExtra("TRACK_TITLE")
        Glide.with(trackArtwork.context).load(intent.getStringExtra("TRACK_ARTWORK") ?: "").into(trackArtwork)

        setupWebView()

        playButton.setOnClickListener { playStream() }
        pauseButton.setOnClickListener { pauseStream() }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
        })
    }

    private fun setupWebView() {
        webViewPlayer.settings.javaScriptEnabled = true
        webViewPlayer.settings.domStorageEnabled = true
        webViewPlayer.visibility = INVISIBLE
        webViewPlayer.webViewClient = WebViewClient()

        url = "${AudiusEndpointUtil.getUsedEndpoint()}/v1/tracks/${trackid}/stream?app_name=StreamFusion"
        Log.d("url_audiusplayer", url)
        webViewPlayer.loadUrl(url)

        webViewPlayer.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webViewPlayer.evaluateJavascript(
                    """
                    (function() {
                        var videoElement = document.querySelector('video');
                        if (videoElement) {
                            videoElement.pause()
                            videoElement.currentTime = 0
                        }
                    })();
                    """.trimIndent(), null
                )
                super.onPageFinished(view, url)
            }
        }
    }

    private fun streamAudiusTrack(trackId: String) {
        webViewPlayer.evaluateJavascript(
            """
            (function() {
                var videoElement = document.querySelector('video');
                if (videoElement) {
                    videoElement.play()
                }
            })();
            """.trimIndent(), null)
        if(AudiusEndpointUtil.getUsedEndpoint() != "null"){
            val api = AudiusEndpointUtil.getApiInstance()
            api.streamTrack(trackId).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val streamUrl = response.raw().request.url.toString()
                        initializeMediaPlayer(streamUrl)
                        Log.d("StreamActivity", streamUrl)
                    } else {
                        Log.e(
                            "StreamActivity",
                            "Stream request failed: ${response.errorBody()?.string()}"
                        )
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("StreamActivity", "API call failed: ${t.message}")
                }
            })
        }
    }

    private fun initializeMediaPlayer(streamUrl: String) {
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(streamUrl)
            prepareAsync()
            setOnPreparedListener {
                seekBar.max = duration
                start()
                updateSeekBar()
            }
            setOnErrorListener { mp, what, extra ->
                Log.e("AudiusStream", "MediaPlayer error: what=$what, extra=$extra")
                true
            }
        }
    }

    private fun playStream() {
        if (isPaused) {
            mediaPlayer?.start()
            isPaused = false
            updateSeekBar()
        } else {
            streamAudiusTrack(trackid)
        }
    }

    private fun pauseStream() {
//        webViewPlayer.evaluateJavascript(
//            """
//            (function() {
//                var videoElement = document.querySelector('video');
//                if (videoElement) {
//                    videoElement.pause()
//                }
//            })();
//            """.trimIndent(), null)
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            timer?.cancel()
            isPaused = true
        }
    }

    private fun updateSeekBar() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (mediaPlayer?.isPlaying == true) {
                        seekBar.progress = mediaPlayer?.currentPosition!!
                    }
                }
            }
        }, 0, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        webViewPlayer.destroy()
        mediaPlayer?.release()
        timer?.cancel()
    }
}
