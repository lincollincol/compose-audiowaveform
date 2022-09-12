package com.linc.audiowaveform.sample.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.Cache
import linc.com.amplituda.callback.AmplitudaErrorListener
import javax.inject.Inject

class AudioManager @Inject constructor(
    private val amplituda: Amplituda,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getAmplitudes(path: String): List<Int> = withContext(ioDispatcher) {
        return@withContext amplituda.processAudio(path, Cache.withParams(Cache.REUSE))
            .get(AmplitudaErrorListener {
                it.printStackTrace()
            })
            .amplitudesAsList()
    }
}