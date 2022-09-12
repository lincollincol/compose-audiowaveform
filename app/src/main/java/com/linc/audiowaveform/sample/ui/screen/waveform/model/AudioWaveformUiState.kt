package com.linc.audiowaveform.sample.ui.screen.waveform.model

data class AudioWaveformUiState(
    val audioDisplayName: String = "",
    val amplitudes: List<Int> = emptyList(),
    val isPlaying: Boolean = false,
    val progress: Float = 0F
)