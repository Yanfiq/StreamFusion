package com.yanfiq.streamfusion.screens

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import kotlinx.coroutines.delay
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PlayAudiusActivity : AppCompatActivity() {
    
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val trackid = intent.getStringExtra("TRACK_ID") ?: "null"
        val trackTitle = intent.getStringExtra("TRACK_TITLE") ?: "null"
        val trackArtist = intent.getStringExtra("TRACK_ARTIST") ?: "null"
        val trackArtwork = intent.getStringExtra("TRACK_ARTWORK") ?: "null"
        Log.d("AudiusStream", "Track ID: "+trackid)

        setContent {
            AudiusPlayScreen(trackId = trackid, trackTitle = trackTitle, trackArtist = trackArtist, trackArtwork = trackArtwork, context = this@PlayAudiusActivity)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudiusPlayScreen(
    trackId: String,
    trackTitle: String,
    trackArtist: String,
    trackArtwork: String,
    context: Context
) {
    var isPaused by remember { mutableStateOf(true) }
    val mediaPlayer = remember { MediaPlayer() }
    var sliderPosition by remember { mutableStateOf(0f) }
    var maxDuration by remember { mutableStateOf(1f) }
    var isPlayerReady by remember { mutableStateOf(false) }

    LaunchedEffect(trackId) {
        playTrack(trackId, mediaPlayer, context){ result ->
            maxDuration = result
            isPlayerReady = true
        }
        while (true) {
            if (!isPaused && mediaPlayer.isPlaying) {
                sliderPosition = mediaPlayer.currentPosition / 1000f
            }
            delay(1000L)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = trackTitle, style = MaterialTheme.typography.titleLarge)
        Text(text = trackArtist, style = MaterialTheme.typography.titleSmall)
        AsyncImage(
            model = trackArtwork,
            contentDescription = trackTitle,
            imageLoader = ImageLoader(context),
            modifier = Modifier.width(600.dp)
                .height(600.dp)
        )
        Slider(value = sliderPosition,
            onValueChange = { newValue ->
                sliderPosition = newValue
                mediaPlayer.seekTo((newValue * 1000).toInt())
            },
            valueRange = 0f..maxDuration,
            enabled = isPlayerReady,
            modifier = Modifier.fillMaxWidth()) // Slider functionality
        Button(
            onClick = {
                isPaused = !isPaused
                if (isPaused) mediaPlayer.pause() else mediaPlayer.start()
            },
            enabled = isPlayerReady,
            modifier = Modifier.width(75.dp).height(75.dp)
        ) {
            Icon(
                imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                contentDescription = "Play/Pause button",
            )
        }
    }
}

private fun playTrack(trackId: String, mediaPlayer: MediaPlayer, context: Context, onResult: (Float) -> Unit) {
    AudiusEndpointUtil.getApiInstance()?.streamTrack(trackId)
        ?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val streamUrl = response.raw().request.url.toString()
                    Log.d("AudiusStream", "Stream URL: " + streamUrl)
                    response.body()
                        ?.use { // Using use to ensure the response body is closed properly
                            mediaPlayer.apply {
                                setAudioAttributes(
                                    AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .build()
                                )
                                setDataSource(streamUrl)
                                prepareAsync()
                                setOnPreparedListener {
                                    onResult(mediaPlayer.duration / 1000f)
                                    Log.e("AudiusStream", "MediaPlayer prepared")
                                }
                                setOnErrorListener { mp, what, extra ->
                                    Log.e(
                                        "AudiusStream",
                                        "MediaPlayer error: what=$what, extra=$extra"
                                    )
                                    true
                                }
                            }
                            Log.d("AudiusStream", streamUrl)
                        } ?: run {
                        Log.e("AudiusStream", "Response body is null")
                    }
                } else {
                    Log.e(
                        "AudiusStream",
                        "Stream request failed: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("AudiusStream", "API call failed: ${t.message}")
            }
        })
}

@Composable
@Preview
private fun Preview(){
    val context: Context = LocalContext.current
    AudiusPlayScreen(trackId = "75746", trackTitle = "Acumalaka", trackArtist = "Acumalaka", trackArtwork = "https://i1.sndcdn.com/artworks-000057356357-9tmqex-t240x240.jpg", context = context)
}