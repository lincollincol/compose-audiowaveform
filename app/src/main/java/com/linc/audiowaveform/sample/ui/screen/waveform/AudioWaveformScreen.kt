package com.linc.audiowaveform.sample.ui.screen.waveform

import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.sample.R
import com.linc.audiowaveform.sample.ui.screen.waveform.model.AudioWaveformUiState
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
        onProgressChanged = viewModel::updateProgress
    )
}

@Composable
fun AudioWaveformScreen(
    uiState: AudioWaveformUiState,
    onPlayClicked: () -> Unit,
    onProgressChanged: (Float) -> Unit,
) {
    val playButtonIcon = if(uiState.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart),
                text = uiState.audioDisplayName,
                style = MaterialTheme.typography.h5
            )
            AudioWaveform(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                progress = uiState.progress,
                amplitudes = uiState.amplitudes,
                onProgressChanged = onProgressChanged
            )
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
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

@Preview
@Composable
private fun AudioWaveformScreenPreview() {
    ComposeaudiowaveformTheme {
        AudioWaveformScreen(
            uiState = AudioWaveformUiState(),
            onPlayClicked = {},
            onProgressChanged = {}
        )
    }
}