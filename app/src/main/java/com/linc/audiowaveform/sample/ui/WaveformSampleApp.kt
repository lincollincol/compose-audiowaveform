package com.linc.audiowaveform.sample.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.compose.rememberNavController
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.infiniteLinearGradient
import com.linc.audiowaveform.model.AmplitudeType
import com.linc.audiowaveform.sample.navigation.AppNavGraph
import com.linc.audiowaveform.sample.ui.theme.ComposeaudiowaveformTheme

@Composable
fun WaveformSampleApp() {
    ComposeaudiowaveformTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AppNavGraph(navHostController = rememberNavController())

//            var waveformProgress by rememberSaveable { mutableStateOf(0.5F) }
//            val endOffset = LocalConfiguration.current.screenWidthDp * LocalDensity.current.density
//            val brush = Brush.infiniteLinearGradient(
//                colors = listOf(Color(0xff22c1c3), Color(0xfffdbb2d)),
//                animation = tween(durationMillis = 6000, easing = LinearEasing),
//                width = endOffset
//            )
//            AudioWaveform(
//                progress = waveformProgress,
//                progressBrush = brush,
//                amplitudeType = AmplitudeType.Avg,
//                amplitudes = amplitudes,
//            ) {
//                waveformProgress = it
//            }
        }
    }
}