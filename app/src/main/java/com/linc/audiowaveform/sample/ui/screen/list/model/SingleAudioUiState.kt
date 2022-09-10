package com.linc.audiowaveform.sample.ui.screen.list.model

import com.linc.audiowaveform.sample.extension.formatAsAudioDuration
import com.linc.audiowaveform.sample.extension.formatAsFileSize
import com.linc.audiowaveform.sample.model.LocalAudio

data class SingleAudioUiState(
    val id: String,
    val displayName: String,
    val size: String,
    val onClick: () -> Unit
)

fun LocalAudio.toUiState(
    onClick: () -> Unit
) = SingleAudioUiState(
    id = id,
    displayName = name,
    size = duration.formatAsAudioDuration + " | " + size.formatAsFileSize,
    onClick = onClick
)