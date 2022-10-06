package com.linc.audiowaveform.sample.ui.screen.waveform

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.AmplitudeType
import com.linc.audiowaveform.model.WaveformAlignment
import com.linc.audiowaveform.sample.R
import com.linc.audiowaveform.sample.ui.screen.waveform.model.AudioWaveformUiState
import com.linc.audiowaveform.sample.ui.screen.waveform.model.getMockPalettes
import com.linc.audiowaveform.sample.ui.screen.waveform.model.getMockStyles
import com.linc.audiowaveform.sample.ui.theme.ComposeaudiowaveformTheme

@Composable
fun AudioWaveformRoute(
    viewModel: AudioWaveformViewModel,
    contentId: String
) {
    LaunchedEffect(contentId) {
        viewModel.loadAudio(contentId)
    }
    AudioWaveformScreen(
        uiState = viewModel.uiState,
        onPlayClicked = viewModel::updatePlaybackState,
        onProgressChange = viewModel::updateProgress
    )
}

@Composable
fun AudioWaveformScreen(
    uiState: AudioWaveformUiState,
    onPlayClicked: () -> Unit,
    onProgressChange: (Float) -> Unit,
) {
    val colorPalettes = getMockPalettes()
    val waveformStyles = getMockStyles()
    var colorPaletteIndex by remember { mutableStateOf(0) }
    var waveformStyle by remember { mutableStateOf(waveformStyles.first()) }
    var waveformAlignment by remember { mutableStateOf(WaveformAlignment.Center) }
    var amplitudeType by remember { mutableStateOf(AmplitudeType.Avg) }
    var spikeWidth by remember { mutableStateOf(4F) }
    var spikePadding by remember { mutableStateOf(2F) }
    var spikeCornerRadius by remember { mutableStateOf(2F) }
    val playButtonIcon by remember(uiState.isPlaying) {
        mutableStateOf(if(uiState.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
    }
    var scrollEnabled by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .verticalScroll(state = rememberScrollState(), enabled = scrollEnabled)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = uiState.audioDisplayName,
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                AudioWaveform(
                    modifier = Modifier.fillMaxWidth(),
                    style = waveformStyle.style,
                    waveformAlignment = waveformAlignment,
                    amplitudeType = amplitudeType,
                    progressBrush = colorPalettes[colorPaletteIndex].progressColor,
                    waveformBrush = colorPalettes[colorPaletteIndex].waveformColor,
                    spikeWidth = Dp(spikeWidth),
                    spikePadding = Dp(spikePadding),
                    spikeRadius = Dp(spikeCornerRadius),
                    progress = uiState.progress,
                    amplitudes = uiState.amplitudes,
                    onProgressChange = {
                        scrollEnabled = false
                        onProgressChange(it)
                    },
                    onProgressChangeFinished = {
                        scrollEnabled = true
                    }
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                LabelSlider(
                    text = stringResource(id = R.string.spike_width),
                    value = spikeWidth,
                    onValueChange = { spikeWidth = it },
                    valueRange = 1.dp.value..24.dp.value
                )
                LabelSlider(
                    text = stringResource(id = R.string.spike_padding),
                    value = spikePadding,
                    onValueChange = { spikePadding = it },
                    valueRange = 0.dp.value..12.dp.value
                )
                LabelSlider(
                    text = stringResource(id = R.string.spike_radius),
                    value = spikeCornerRadius,
                    onValueChange = { spikeCornerRadius = it },
                    valueRange = 0.dp.value..12.dp.value
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WaveformAlignment.values().forEach {
                        RadioGroupItem(
                            text = it.name,
                            selected = waveformAlignment == it,
                            onClick = { waveformAlignment = it }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AmplitudeType.values().forEach {
                        RadioGroupItem(
                            text = it.name,
                            selected = amplitudeType == it,
                            onClick = { amplitudeType = it }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    waveformStyles.forEach {
                        RadioGroupItem(
                            text = it.label,
                            selected = it.label == waveformStyle.label,
                            onClick = {
                                waveformStyle = it
                            }
                        )
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                colorPalettes.forEach {
                    ColorPaletteItem(
                        selected = it.label == colorPalettes[colorPaletteIndex].label,
                        progressColor = it.progressColor,
                        waveformColor = it.waveformColor
                    ) {
                        colorPaletteIndex = colorPalettes.indexOf(it)
                    }
                }
            }
            FloatingActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = onPlayClicked
            ) {
                Icon(
                    painter = painterResource(id = playButtonIcon),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun LabelSlider(
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    text: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange
        )
    }
}

@Composable
fun RadioGroupItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text)
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}

@Composable
fun ColorPaletteItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    progressColor: Brush,
    waveformColor: Brush,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Box(modifier = Modifier
            .height(24.dp)
            .weight(1F)
            .background(progressColor)
        )
        Box(modifier = Modifier
            .height(24.dp)
            .weight(1F)
            .background(waveformColor)
        )
    }
}

@Preview
@Composable
private fun AudioWaveformScreenPreview() {
    ComposeaudiowaveformTheme {
        AudioWaveformScreen(
            uiState = AudioWaveformUiState(),
            onPlayClicked = {},
            onProgressChange = {}
        )
    }
}