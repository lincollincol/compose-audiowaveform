package com.linc.audiowaveform.sample.ui.screen.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.linc.audiowaveform.sample.data.AudioRepository
import com.linc.audiowaveform.sample.model.LocalAudio
import com.linc.audiowaveform.sample.model.NavDestination
import com.linc.audiowaveform.sample.ui.screen.list.model.AudioListUiState
import com.linc.audiowaveform.sample.ui.screen.list.model.SingleAudioUiState
import com.linc.audiowaveform.sample.ui.screen.list.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Collections.list
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class AudioListViewModel @Inject constructor(
    audioRepository: AudioRepository
) : ViewModel() {

    var navDestination: String? by mutableStateOf(null)
        private set

    val audioItemsUiState: Flow<PagingData<SingleAudioUiState>> =
        audioRepository.loadAudioFiles()
            .map { pagedData ->
                pagedData.map { localAudio ->
                    localAudio.toUiState { selectAudio(localAudio) }
                }
            }
            .cachedIn(viewModelScope)

    private fun selectAudio(localAudio: LocalAudio) {
        navDestination = NavDestination.AudioWaveform.createRoute(localAudio.id)
    }

    fun clearNavDestination() {
        navDestination = null
    }
}