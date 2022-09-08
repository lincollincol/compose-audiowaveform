package com.linc.audiowaveform.sample.data

import android.net.Uri
import com.linc.audiowaveform.sample.model.LocalAudio
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private val localMediaDataSource: LocalMediaDataSource,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun loadAudioFiles(): List<LocalAudio> = withContext(ioDispatcher) {
        localMediaDataSource.loadAudioFiles()
    }

    suspend fun loadAudioByContentId(id: String): LocalAudio? = withContext(ioDispatcher) {
        localMediaDataSource.loadAudioById(id)
    }

    suspend fun loadAudioAmplitudes(uri: Uri): List<Int> = withContext(ioDispatcher) {
        localMediaDataSource.loadAudioAmplitudes(uri)
    }

}