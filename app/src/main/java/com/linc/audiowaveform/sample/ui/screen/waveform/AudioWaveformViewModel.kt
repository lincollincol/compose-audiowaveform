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
import kotlinx.coroutines.flow.catch
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
        uiState = uiState.copy(isPlaying = !uiState.isPlaying)
    }

    fun loadAudio(contentId: String) {
        viewModelScope.launch {
            try {
                currentLocalAudio = audioRepository.loadAudioByContentId(contentId) ?: return@launch
                currentLocalAudio?.let(playbackManager::setAudio)
                launch { currentLocalAudio?.let { loadAudioAmplitudes(it) } }
                launch { observePlaybackEvents() }
                uiState = uiState.copy(
                    audioDisplayName = currentLocalAudio?.displayName?.substringBefore('.')?:"",
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

    private suspend fun observePlaybackEvents() {
        playbackManager.events.collectLatest {
            when(it) {
                is AudioPlaybackManager.Event.PositionChanged -> updatePlaybackProgress(it.position)
                is AudioPlaybackManager.Event.PlayingChanged -> println("Position ${it.isPlaying}")
            }
        }
    }

    private fun updatePlaybackProgress(position: Long) {
        val audio = currentLocalAudio ?: return
        uiState = uiState.copy(progress = position.toFloat() / audio.duration)
    }
}