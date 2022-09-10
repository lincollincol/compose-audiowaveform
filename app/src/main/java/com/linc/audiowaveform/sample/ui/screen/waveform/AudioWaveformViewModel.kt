package com.linc.audiowaveform.sample.ui.screen.waveform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linc.audiowaveform.sample.android.AudioPlaybackManager
import com.linc.audiowaveform.sample.data.AudioRepository
import com.linc.audiowaveform.sample.model.LocalAudio
import com.linc.audiowaveform.sample.ui.screen.waveform.model.AudioWaveformUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioWaveformViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val playbackManager: AudioPlaybackManager
) : ViewModel() {

    var uiState: AudioWaveformUiState by mutableStateOf(AudioWaveformUiState())
        private set

    private var currentLocalAudio: LocalAudio? = null

    init {
        playbackManager.initializeController()
    }

    override fun onCleared() {
        super.onCleared()
        playbackManager.releaseController()
    }

    fun updateProgress(progress: Float) {
        val position = currentLocalAudio?.duration?.times(progress)?.toLong() ?: 0L
        playbackManager.seekTo(position)
        uiState = uiState.copy(progress = progress)
    }

    fun updatePlaybackState() {
        when {
            uiState.isPlaying -> playbackManager.pause()
            else -> playbackManager.play()
        }
    }

    fun loadAudio(contentId: String) {
        viewModelScope.launch {
            try {
                currentLocalAudio = audioRepository.loadAudioByContentId(contentId) ?: return@launch
                currentLocalAudio?.let(playbackManager::setAudio)
                launch { currentLocalAudio?.let { loadAudioAmplitudes(it) } }
                launch { observePlaybackEvents() }
                uiState = uiState.copy(
                    audioDisplayName = currentLocalAudio?.nameWithoutExtension.orEmpty(),
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun loadAudioAmplitudes(localAudio: LocalAudio) {
        try {
            val amplitudes = audioRepository.loadAudioAmplitudes(localAudio)
            uiState = uiState.copy(amplitudes = amplitudes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun observePlaybackEvents() {
        playbackManager.events.collectLatest {
            when(it) {
                is AudioPlaybackManager.Event.PositionChanged -> updatePlaybackProgress(it.position)
                is AudioPlaybackManager.Event.PlayingChanged -> updatePlayingState(it.isPlaying)
            }
        }
    }

    private fun updatePlaybackProgress(position: Long) {
        val audio = currentLocalAudio ?: return
        uiState = uiState.copy(progress = position.toFloat() / audio.duration)
    }

    private fun updatePlayingState(isPlaying: Boolean) {
        uiState = uiState.copy(isPlaying = isPlaying)
    }
}