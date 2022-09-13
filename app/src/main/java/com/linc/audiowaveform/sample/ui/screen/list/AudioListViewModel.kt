package com.linc.audiowaveform.sample.ui.screen.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linc.audiowaveform.sample.data.AudioRepository
import com.linc.audiowaveform.sample.model.LocalAudio
import com.linc.audiowaveform.sample.model.NavDestination
import com.linc.audiowaveform.sample.model.PermissionsState
import com.linc.audiowaveform.sample.ui.screen.list.model.AudioListUiState
import com.linc.audiowaveform.sample.ui.screen.list.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioListViewModel @Inject constructor(
    private val audioRepository: AudioRepository
) : ViewModel() {

    var navDestination: String? by mutableStateOf(null)
        private set

    var uiState: AudioListUiState by mutableStateOf(AudioListUiState())
        private set

    private var loadAudioJob: Job? = null

    fun updateSearchQuery(query: String) {
        uiState = uiState.copy(searchQuery = query)
        loadAudioFiles(query)
    }

    fun updatePermissionsState(granted: Boolean) {
        uiState = uiState.copy(
            permissionsState = when {
                granted -> PermissionsState.Granted
                else -> PermissionsState.Denied
            }
        )
        if(granted) {
            loadAudioFiles()
        }
    }

    private fun loadAudioFiles(query: String? = null) {
        loadAudioJob?.cancel()
        loadAudioJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoadingAudios = true)
                val audioFiles = audioRepository.loadAudioFiles(query ?: uiState.searchQuery)
                    .map { it.toUiState { selectAudio(it) } }
                uiState = uiState.copy(audioFiles = audioFiles)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                uiState = uiState.copy(isLoadingAudios = false)
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