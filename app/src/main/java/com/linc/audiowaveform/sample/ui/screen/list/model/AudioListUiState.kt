package com.linc.audiowaveform.sample.ui.screen.list.model

import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems

data class AudioListUiState(
    val audioFiles: PagingData<SingleAudioUiState> = PagingData.empty()
)