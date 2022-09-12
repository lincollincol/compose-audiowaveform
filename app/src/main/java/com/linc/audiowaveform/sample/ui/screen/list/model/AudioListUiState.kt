package com.linc.audiowaveform.sample.ui.screen.list.model

import com.linc.audiowaveform.sample.model.PermissionsState

data class AudioListUiState(
    val permissionsState: PermissionsState = PermissionsState.Unknown,
    val searchQuery: String = "",
    val audioFiles: List<SingleAudioUiState> = emptyList(),
    val isLoadingAudios: Boolean = false
)