package com.yanfiq.streamfusion.presentation.screens.player

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.presentation.ui.theme.AppTheme
import kotlinx.coroutines.delay
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpSize

val interactionSource = MutableInteractionSource()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    context: Context,
    trackTitle: String,
    trackArtist: String,
    trackArtwork: String,
    trackDuration: Int,
    trackElapsedTime: Int,
    isPlayerReady: Boolean,
    isPlaying: Boolean,
    onPause: () -> Unit,
    onPlay: () -> Unit,
    onSeek: (Boolean, Int) -> Unit
) {
    var sliderValue by remember { mutableStateOf(trackElapsedTime) }
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(25.dp)
            ) {
                AsyncImage(
                    model = trackArtwork,
                    contentDescription = trackTitle,
                    placeholder = painterResource(id = R.drawable.music_placeholder),
                    imageLoader = ImageLoader(context),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(25.dp))
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(text = trackTitle, style = MaterialTheme.typography.titleLarge)
                    Text(text = trackArtist, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(10.dp))
                SliderWithoutThumb(
                    value = trackElapsedTime.toFloat(),
                    maxValue = trackDuration.toFloat(),
                    onValueChange = {newValue ->
                        sliderValue = newValue.toInt()
                    },
                    isSeeking = {value ->
                        onSeek(value, sliderValue)
                    }
                )
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    val elapsed_minutes: Int = (trackElapsedTime/60f).toInt()
                    val elapsed_seconds: Int = (trackElapsedTime - elapsed_minutes*60)
                    val elapsedString = "${elapsed_minutes}:${String.format("%02d", elapsed_seconds)}"

                    val remains_minutes: Int = ((trackDuration-trackElapsedTime)/60f).toInt()
                    val remains_seconds: Int = ((trackDuration-trackElapsedTime) - remains_minutes*60)
                    val remainsString = "-${remains_minutes}:${String.format("%02d", remains_seconds)}"
                    Text(text = elapsedString)
                    Text(text = remainsString)
                }
                Button(
                    onClick = {
                        if (isPlaying) onPause() else onPlay()
                    },
                    enabled = isPlayerReady,
                    modifier = Modifier
                        .width(75.dp)
                        .height(75.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (!isPlaying) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = "Play/Pause button",
                        modifier = Modifier.fillMaxSize(0.60f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PlayScreenPreview(){
    PlayScreen(
        context = LocalContext.current,
        trackTitle = "Test",
        trackArtist = "Test",
        trackArtwork = "",
        trackDuration = 100,
        trackElapsedTime = 59,
        isPlayerReady = true,
        isPlaying = false,
        onPause = { /*TODO*/ },
        onPlay = { /*TODO*/ },
        onSeek = {a, b -> })
}

@Composable
private fun SliderWithoutThumb(value: Float, maxValue: Float, onValueChange: (Float) -> Unit, isSeeking: (Boolean) -> Unit) {
    BoxWithConstraints ( //container
        modifier = Modifier
            .fillMaxWidth()
            .height(7.5.dp),
        contentAlignment = Alignment.Center
    ){
        val sliderActiveWidth = maxWidth * value/maxValue
        val sliderMaxWidth = maxWidth * 1f
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp,
                        bottomStart = 10.dp,
                        bottomEnd = 10.dp
                    )
                )
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .fillMaxWidth()
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val xInDp = offset.x.toDp()
                            val newValue = xInDp / sliderMaxWidth * maxValue
                            isSeeking(true)
                            onValueChange(newValue)
                        },
                        onDragEnd = {
                            isSeeking(false)
                        },
                        onDrag = { change, dragAmount ->
                            val currentPosition = change.position
                            val xInDp = currentPosition.x.toDp()
                            val newValue = xInDp / sliderMaxWidth * maxValue
                            isSeeking(true)
                            onValueChange(newValue)
                            change.consume()
                        }
                    )
                }
                .pointerInput(Unit){
                    detectTapGestures(
                        onTap = {offset ->
                            val xInDp = offset.x.toDp()
                            val newValue = xInDp / sliderMaxWidth * maxValue
                            isSeeking(false)
                            onValueChange(newValue)
                        }
                    )
                },
            contentAlignment = Alignment.TopStart
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .width(sliderActiveWidth)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
@Preview
private fun sliderPreview(){
    var value by remember { mutableStateOf(50f)}
    val maxValue = 100f

    Box (
        modifier = Modifier
            .width(150.dp)
            .height(25.dp)
            .background(Color.White)
            .padding(10.dp)
    ){
        SliderWithoutThumb(value = value, maxValue = maxValue,
            onValueChange = {newValue ->
                value = newValue
            },
            isSeeking = {})
    }
}

@Composable
fun TransitionAnimationDemo() {
    var selected by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = selected, label = "BoxTransition")

    val color by transition.animateColor(label = "ColorAnimation") { state ->
        if (state) Color.Green else Color.Gray
    }

    val size by transition.animateDp(label = "SizeAnimation") { state ->
        if (state) 100.dp else 50.dp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { selected = !selected }) {
            Text("Toggle")
        }

        Box(
            modifier = Modifier
                .size(size)
                .background(color)
        )
    }
}

@Preview
@Composable
fun PreviewTransitionAnimationDemo() {
    TransitionAnimationDemo()
}
