package com.linc.audiowaveform.sample.ui.screen.waveform

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linc.audiowaveform.sample.android.AudioPlaybackManager
import com.linc.audiowaveform.sample.data.AudioRepository
import com.linc.audiowaveform.sample.model.LocalAudio
import com.linc.audiowaveform.sample.model.NavDestination
import com.linc.audiowaveform.sample.ui.screen.list.model.AudioListUiState
import com.linc.audiowaveform.sample.ui.screen.list.model.toUiState
import com.linc.audiowaveform.sample.ui.screen.waveform.model.AudioWaveformUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioWaveformViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val playbackManager: AudioPlaybackManager
) : ViewModel() {

    var uiState: AudioWaveformUiState by mutableStateOf(AudioWaveformUiState())
        private set

    init {
        try {
            playbackManager.initializeController()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateProgress(progress: Float) {
        uiState = uiState.copy(progress = progress)
    }

    fun updatePlaybackState() {
        uiState = uiState.copy(isPlaying = !uiState.isPlaying)
        playbackManager.setAudio(uri!!, "")
        when {
            uiState.isPlaying -> playbackManager.pause()
            else -> playbackManager.play()
        }
    }

    var uri: Uri? = null

    fun loadAudio(contentId: String) {
        viewModelScope.launch {
            try {
                val localAudio = audioRepository.loadAudioByContentId(contentId) ?: return@launch
                playbackManager.setAudio(localAudio)
                uri = localAudio.uri
                launch { loadAudioAmplitudes(localAudio) }
                uiState = uiState.copy(
                    audioDisplayName = localAudio.displayName.substringBefore('.'),
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun loadAudioAmplitudes(localAudio: LocalAudio) {
        val amplitudes = audioRepository.loadAudioAmplitudes(localAudio.uri)
        uiState = uiState.copy(amplitudes = amplitudes)
    }
}