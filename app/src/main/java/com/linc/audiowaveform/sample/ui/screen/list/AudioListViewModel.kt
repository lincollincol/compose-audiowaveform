package com.linc.audiowaveform.sample.ui.screen.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linc.audiowaveform.sample.data.AudioRepository
import com.linc.audiowaveform.sample.model.LocalAudio
import com.linc.audiowaveform.sample.model.NavDestination
import com.linc.audiowaveform.sample.ui.screen.list.model.AudioListUiState
import com.linc.audiowaveform.sample.ui.screen.list.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class AudioListViewModel @Inject constructor(
    private val audioRepository: AudioRepository
) : ViewModel() {

    var uiState: AudioListUiState by mutableStateOf(AudioListUiState())
        private set

    var navDestination: String? by mutableStateOf(null)
        private set

    init {
        loadAudioFiles()
    }

    private fun loadAudioFiles() {
        viewModelScope.launch {
            try {
                val audioFiles = audioRepository.loadAudioFiles()
                    .map { localAudio ->
                        localAudio.toUiState { selectAudio(localAudio) }
                    }
                uiState = uiState.copy(audioFiles = audioFiles)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun selectAudio(localAudio: LocalAudio) {
        navDestination = NavDestination.AudioWaveform.createRoute(localAudio.id)
    }

    fun clearNavDestination() {
        navDestination = null
    }
}