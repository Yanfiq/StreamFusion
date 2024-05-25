package com.yanfiq.streamfusion.ui.search.audius

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusApi
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlayAudiusActivity : AppCompatActivity() {

    private lateinit var trackid: String
    private lateinit var trackTitle: TextView
    private lateinit var trackArtwork: ImageView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_play_audius)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        trackTitle = findViewById(R.id.track_title)
        trackArtwork = findViewById(R.id.track_artwork)
        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)

        trackid = intent.getStringExtra("TRACK_ID") ?: ""

        playButton.setOnClickListener { streamAudiusTrack(trackid) }
        pauseButton.setOnClickListener { pauseStream() }
    }

    private fun streamAudiusTrack(trackId: String) {
        AudiusEndpointUtil.getApiInstance { api ->
            if (api != null) {
                api.streamTrack(trackId).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val streamUrl = response.raw().request.url.toString()
                            playStream(streamUrl)
                        } else {
                            Log.e("StreamActivity", "Stream request failed: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("StreamActivity", "API call failed: ${t.message}")
                    }
                })
            } else {
                Log.e("StreamActivity", "No working endpoint found")
            }
        }
    }

    private fun playStream(streamUrl: String) {
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(streamUrl)
            prepareAsync()
            setOnPreparedListener {
                start()
            }
            setOnErrorListener { mp, what, extra ->
                Log.e("AudiusStream", "MediaPlayer error: what=$what, extra=$extra")
                true
            }
        }
    }

    private fun pauseStream() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }

}