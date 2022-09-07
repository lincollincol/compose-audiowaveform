package com.linc.audiowaveform.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.infiniteHorizontalGradient
import com.linc.audiowaveform.infiniteLinearGradient
import com.linc.audiowaveform.infiniteVerticalGradient
import com.linc.audiowaveform.model.AmplitudeType
import com.linc.audiowaveform.sample.ui.theme.ComposeaudiowaveformTheme
import linc.com.amplituda.Amplituda
import linc.com.amplituda.Cache
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val amplituda = Amplituda(this)
        amplituda.setLogConfig(Log.ERROR, true)
        val amplitudes = amplituda.processAudio(
//            "/storage/9016-4EF8/MUSIC/Palace - Heaven Up There.mp3",
            "/storage/9016-4EF8/MUSIC/Kygo - Only Us.mp3",
            Cache.withParams(Cache.REUSE)
        ).get().amplitudesAsList()

        setContent {
            ComposeaudiowaveformTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    var waveformProgress by rememberSaveable { mutableStateOf(0.5F) }
                    val endOffset = LocalConfiguration.current.screenWidthDp * LocalDensity.current.density
                    val brush = Brush.infiniteLinearGradient(
                        colors = listOf(Color(0xff22c1c3), Color(0xfffdbb2d)),
                        animation = tween(durationMillis = 6000, easing = LinearEasing),
                        width = endOffset
                    )
                    AudioWaveform(
                        progress = waveformProgress,
                        progressBrush = brush,
                        amplitudeType = AmplitudeType.Avg,
                        amplitudes = amplitudes,
                    ) {
                        waveformProgress = it
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeaudiowaveformTheme {
        Greeting("Android")
    }
}

/*
var amplitudes2 by remember { mutableStateOf(listOf<Int>()) }
                    val infiniteTransition = rememberInfiniteTransition()
                    val offset by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 200f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )

                    val brush = Brush.linearGradient(
                        listOf(Color(0xff22c1c3), Color(0xfffdbb2d)),
                        start = Offset(offset, offset),
                        end = Offset(offset + 100F, offset + 100F),
                        tileMode = TileMode.Mirror
                    )
 */