package com.yanfiq.streamfusion.ui.search.youtube

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.yanfiq.streamfusion.R
import retrofit2.Call
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask
import java.util.regex.Pattern

class PlayYoutubeActivity : AppCompatActivity() {

    private lateinit var videoId: String
    private lateinit var videoTitle: String
    private lateinit var trackTitle: TextView
    private lateinit var trackArtwork: ImageView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var url: String
    private var timer: Timer? = null
    private var isPaused = false
    private lateinit var youTubePlayerView: YouTubePlayerView
    private lateinit var youTubePlayer_reference: YouTubePlayer
    private lateinit var youtubePlayerTracker: YouTubePlayerTracker
    private lateinit var lyricsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_youtube)
        videoId = intent.getStringExtra("VIDEO_ID") ?: ""
        videoTitle = intent.getStringExtra("VIDEO_TITLE") ?: ""

        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)
        youTubePlayerView = findViewById(R.id.youtube_player_view)
        lifecycle.addObserver(youTubePlayerView)
        seekBar = findViewById(R.id.seekBar)
        lyricsTextView = findViewById(R.id.lyrics)

        playButton.setOnClickListener { playStream() }
        pauseButton.setOnClickListener { pauseStream() }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    youTubePlayer_reference.seekTo(progress.toFloat())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
        })

        youtubePlayerTracker = YouTubePlayerTracker()
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f)
                youTubePlayer.pause()
                isPaused = true
                youTubePlayer.addListener(youtubePlayerTracker)
            }
        })
        youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayerCallback = object :
            YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                youTubePlayer_reference = youTubePlayer
                youTubePlayerView.enableAutomaticInitialization = false
                IFramePlayerOptions.Builder().controls(0).build()
            }
        })


        seekBar.max = intent.getStringExtra("VIDEO_DURATION")?.let { parseISODurationToSeconds(it) }!!
    }

    private fun playStream() {
        if (isPaused) {
            youTubePlayer_reference.play()
            isPaused = false
            updateSeekBar()
        } else {

        }
    }

    private fun pauseStream() {
        if(youtubePlayerTracker.state == PlayerConstants.PlayerState.PLAYING){
            youTubePlayer_reference.pause()
            timer?.cancel()
            isPaused = true
        }
    }

    private fun updateSeekBar() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (isPaused == false) {
                        seekBar.progress = youtubePlayerTracker.currentSecond.toInt()
                    }
                }
            }
        }, 0, 1000)
    }

    private fun parseISODurationToSeconds(duration: String): Int {
        val pattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?")
        val matcher = pattern.matcher(duration)

        var totalSeconds = 0

        if (matcher.matches()) {
            val hours = matcher.group(1)?.toIntOrNull() ?: 0
            val minutes = matcher.group(2)?.toIntOrNull() ?: 0
            val seconds = matcher.group(3)?.toIntOrNull() ?: 0

            totalSeconds = hours * 3600 + minutes * 60 + seconds
        }

        return totalSeconds
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayerView.release()
    }
}