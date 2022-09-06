package com.linc.audiowaveform

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.linc.audiowaveform.model.WaveformAlignment

const val MinSpikeHeight: Float = 1F
const val DefaultGraphicsLayerAlpha: Float = 0.99F

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AudioWaveform(
    modifier: Modifier = Modifier,
    waveformBrush: Brush = Brush.linearGradient(listOf(Color.White, Color.White)),
    progressBrush: Brush = Brush.linearGradient(listOf(Color.Blue, Color.Blue)),
    waveformAlignment: WaveformAlignment = WaveformAlignment.Center,
    spikeWidth: Dp = 4.dp,
    spikeRadius: Dp = 2.dp,
    spikePadding: Dp = 1.dp,
    progress: Float = 0F,
    samples: List<Int>,
    onProgressChanged: (Float) -> Unit
) {
    var canvasSize by remember { mutableStateOf(Size(0f, 0f)) }
    var spikes by remember { mutableStateOf(0F) }
    val amplitudes = remember(samples, spikes) {
        getWaveformAmplitudes(
            samples = samples,
            spikes = spikes.toInt(),
            minHeight = MinSpikeHeight,
            maxHeight = canvasSize.height.coerceAtLeast(MinSpikeHeight)
        )
    }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(72.dp)
            .graphicsLayer(alpha = DefaultGraphicsLayerAlpha)
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        if (it.x in 0F..canvasSize.width) {
                            onProgressChanged(it.x / canvasSize.width)
                            return@pointerInteropFilter true
                        }
                    }
                }
                return@pointerInteropFilter false
            }
            .then(modifier)
    ) {
        canvasSize = size
        spikes = size.width / (spikeWidth.toPx() + spikePadding.toPx())
        amplitudes.forEachIndexed { index, amplitude ->
            drawRoundRect(
                brush = waveformBrush,
                topLeft = Offset(
                    x = index * (spikeWidth.toPx() + spikePadding.toPx()),
                    y = when(waveformAlignment) {
                        WaveformAlignment.Top -> 0F
                        WaveformAlignment.Bottom -> size.height - amplitude
                        WaveformAlignment.Center -> size.height / 2F - amplitude / 2F
                    }
                ),
                size = Size(spikeWidth.toPx(), amplitude),
                cornerRadius = CornerRadius(spikeRadius.toPx(), spikeRadius.toPx()),
            )
            drawRect(
                brush = progressBrush,
                size = Size(progress * size.width, size.height),
                blendMode = BlendMode.SrcAtop
            )
        }
    }
}

private fun getWaveformAmplitudes(
    samples: List<Int>,
    spikes: Int,
    minHeight: Float,
    maxHeight: Float,
): List<Float> {
    return when {
        samples.isEmpty() || samples.count() < spikes || spikes == 0 -> List(spikes) { minHeight }
        else -> samples
            .chunked(samples.count() / spikes)
            .map { (it.average().toFloat() * 2).coerceIn(minHeight, maxHeight) }
    }
}