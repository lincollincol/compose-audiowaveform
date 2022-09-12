package com.linc.audiowaveform.sample.ui.screen.waveform.model

import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import java.util.*

data class WaveformStyle(
    val label: String,
    val style: DrawStyle
)

fun getMockStyles() = listOf(
    WaveformStyle("Fill", Fill),
    WaveformStyle("Stroke", Stroke(width = 1f)),
    WaveformStyle("Dash", Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5F, 5F))))
)