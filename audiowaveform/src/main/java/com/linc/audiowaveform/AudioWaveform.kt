package com.linc.audiowaveform

import android.view.MotionEvent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.linc.audiowaveform.model.AmplitudeType
import com.linc.audiowaveform.model.WaveformAlignment
import kotlinx.coroutines.launch

const val MinProgress: Float = 0F
const val MaxProgress: Float = 1F

const val MinSpikeHeight: Float = 1F
const val DefaultGraphicsLayerAlpha: Float = 0.99F

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AudioWaveform(
    modifier: Modifier = Modifier,
    style: DrawStyle = Fill,
    waveformBrush: Brush = SolidColor(Color.White),
    progressBrush: Brush = SolidColor(Color.Blue),
    waveformAlignment: WaveformAlignment = WaveformAlignment.Center,
    amplitudeType: AmplitudeType = AmplitudeType.Avg,
    spikeAnimationSpec: AnimationSpec<Float> = tween(500),
    spikeWidth: Dp = 4.dp,
    spikeRadius: Dp = 2.dp,
    spikePadding: Dp = 1.dp,
    progress: Float = 0F,
    amplitudes: List<Int>,
    onProgressChanged: (Float) -> Unit
) {
    var canvasSize by remember { mutableStateOf(Size(0f, 0f)) }
    var spikes by remember { mutableStateOf(0F) }
    val pixelAmplitudes = remember(amplitudes, spikes, amplitudeType) {
        getPixelAmplitudes(
            amplitudeType = amplitudeType,
            amplitudes = amplitudes,
            spikes = spikes.toInt(),
            minHeight = MinSpikeHeight,
            maxHeight = canvasSize.height.coerceAtLeast(MinSpikeHeight)
        )
    }.map { animateFloatAsState(it, spikeAnimationSpec).value }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(48.dp)
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
        pixelAmplitudes.forEachIndexed { index, amplitude ->
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
                style = style
            )
            drawRect(
                brush = progressBrush,
                size = Size(
                    width = progress.coerceIn(MinProgress, MaxProgress) * size.width,
                    height = size.height
                ),
                blendMode = BlendMode.SrcAtop
            )
        }
    }
}

private fun getPixelAmplitudes(
    amplitudeType: AmplitudeType,
    amplitudes: List<Int>,
    spikes: Int,
    minHeight: Float,
    maxHeight: Float
): List<Float> {
    return when {
        amplitudes.isEmpty() || spikes == 0 -> List(spikes) { minHeight }
        amplitudes.count() < spikes -> amplitudes.map(Int::toFloat)
        else -> amplitudes.chunked(amplitudes.count() / spikes)
            .map {
                when(amplitudeType) {
                    AmplitudeType.Avg -> it.average()
                    AmplitudeType.Max -> it.max()
                    AmplitudeType.Min -> it.min()
                }.toFloat().times(2).coerceIn(minHeight, maxHeight)
            }
    }
}