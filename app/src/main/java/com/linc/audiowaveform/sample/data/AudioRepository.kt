package com.linc.audiowaveform.sample.data

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.linc.audiowaveform.sample.model.LocalAudio
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private val localMediaDataSource: LocalMediaDataSource,
    private val ioDispatcher: CoroutineDispatcher
) {

    companion object {
        private const val PAGE_SIZE = 10
    }

    fun loadAudioFiles(): Flow<PagingData<LocalAudio>> =
        Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { LocalMediaPagingSource(localMediaDataSource) }
        ).flow

    suspend fun loadAudioByContentId(id: String): LocalAudio? = withContext(ioDispatcher) {
        localMediaDataSource.loadAudioById(id)
    }

    suspend fun loadAudioAmplitudes(localAudio: LocalAudio): List<Int> = withContext(ioDispatcher) {
        localMediaDataSource.loadAudioAmplitudes(localAudio.path)
    }

}