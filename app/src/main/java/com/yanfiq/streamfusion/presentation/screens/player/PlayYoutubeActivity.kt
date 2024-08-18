package com.yanfiq.streamfusion.presentation.screens.player

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.yanfiq.streamfusion.presentation.ui.theme.AppTheme
import kotlinx.coroutines.delay

class PlayYoutubeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            YoutubePlayScreen(
                Title = intent.getStringExtra("VIDEO_TITLE") ?: "NULL",
                Channel = intent.getStringExtra("VIDEO_CREATOR") ?: "NULL",
                videoID = intent.getStringExtra("VIDEO_ID") ?: "NULL",
                maxDuration = intent.getIntExtra("VIDEO_DURATION", 0)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YoutubePlayScreen(Title: String, Channel: String, videoID: String, maxDuration: Int){
    var isPaused by remember { mutableStateOf(true) }
    var sliderPosition by remember { mutableStateOf(0f) }
    var isPlayerReady by remember { mutableStateOf(false) }
    var youtubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
    var youtubeTracker by remember { mutableStateOf<YouTubePlayerTracker?>(null) }

    AppTheme {
        Surface (
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(25.dp)) {
                Player(videoID = videoID,
                    player = { player ->
                        youtubePlayer = player
                        isPlayerReady = true
                }, tracker = { tracker ->
                        youtubeTracker = tracker
                        sliderPosition = tracker.currentSecond
                })
                Spacer(modifier = Modifier.height(25.dp))
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = Title,
                        style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = Channel,
                        style = MaterialTheme.typography.titleMedium)
                }
                Slider(
                    value = sliderPosition,
                    onValueChange = { newValue ->
                        sliderPosition = newValue
                        youtubePlayer?.seekTo(newValue)
                    },
                    valueRange = 0f..maxDuration.toFloat(),
                    enabled = isPlayerReady,
                    thumb = {
                        SliderDefaults.Thumb( //androidx.compose.material3.SliderDefaults
                            interactionSource = interactionSource,
                            thumbSize = DpSize(0.dp, 0.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    val elapsed_minutes: Int = (sliderPosition.toInt()/60f).toInt()
                    val elapsed_seconds: Int = (sliderPosition.toInt() - elapsed_minutes*60)
                    val elapsedString = "${elapsed_minutes}:${String.format("%02d", elapsed_seconds)}"

                    val remains_minutes: Int = ((maxDuration-sliderPosition.toInt())/60f).toInt()
                    val remains_seconds: Int = ((maxDuration-sliderPosition.toInt()) - remains_minutes*60)
                    val remainsString = "-${remains_minutes}:${String.format("%02d", remains_seconds)}"
                    Text(text = elapsedString)
                    Text(text = remainsString)
                }
                Button(
                    onClick = {
                        isPaused = !isPaused
                        if(isPaused) youtubePlayer?.pause() else youtubePlayer?.play()
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

    LaunchedEffect(youtubeTracker) {
        while (true) {
            if(youtubeTracker != null){
                youtubeTracker?.let {
                    sliderPosition = it.currentSecond
                    delay(1000L)  // Update the slider position every second
                }
            }else{
                delay(1000L)  // Update the slider position every second
            }
        }
    }
}

@Composable
fun Player(videoID: String, player: (YouTubePlayer) -> Unit, tracker: (YouTubePlayerTracker) -> Unit) {
    val context = LocalContext.current
    val youtubePlayer = remember {
        YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false
            var options = IFramePlayerOptions.Builder().controls(0).build()
            initialize(
                object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(videoID, 0f)
                        val youTubePlayerTracker = YouTubePlayerTracker()
                        youTubePlayer.addListener(youTubePlayerTracker)
                        player(youTubePlayer)
                        tracker(youTubePlayerTracker)
                    }
                },
                options
            )
        }
    }

    AndroidView(
        {
            youtubePlayer
        }, modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
}

@Preview
@Composable
private fun YoutubeScreenPreview(){
    YoutubePlayScreen("Acumalaka", "Acumalaka", "Acumalaka", 100)
}