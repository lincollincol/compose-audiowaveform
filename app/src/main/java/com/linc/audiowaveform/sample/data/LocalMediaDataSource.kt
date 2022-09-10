package com.linc.audiowaveform.sample.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.linc.audiowaveform.sample.model.LocalAudio
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.Cache
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject


class LocalMediaDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val amplituda: Amplituda,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val contentResolver: ContentResolver get() = context.contentResolver
    private val audioProjection: Array<String> get() = arrayOf(
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.DATE_ADDED,
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.AudioColumns.SIZE
    )

    suspend fun loadAudioAmplitudes(path: String): List<Int> {
        return amplituda.processAudio(path, Cache.withParams(Cache.REUSE)).get().amplitudesAsList()
    }

    suspend fun loadAudioById(id: String): LocalAudio? = withContext(ioDispatcher) {
        val mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media._ID + "=?"
        val selectionArgs = arrayOf(id)
        val cursor = contentResolver.query(mediaContentUri, audioProjection, selection, selectionArgs, null)
        return@withContext cursor?.use {
            if (it.moveToFirst()) it.toLocalAudio() else null
        }
    }

    suspend fun loadAudioFiles(
        page: Int,
        pageSize: Int
    ) : List<LocalAudio?> = withContext(ioDispatcher) {
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val content = mutableListOf<LocalAudio?>()
        val offset = (page - 1) * pageSize
        val cursor = contentResolver.query(
            contentUri,
            audioProjection,
            null,
            null,
            "${MediaStore.MediaColumns.DATE_ADDED} DESC LIMIT $pageSize OFFSET $offset"
        )
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    content.add(it.toLocalAudio())
                } while (it.moveToNext())
            }
        }
        return@withContext content
    }

    private fun Cursor.toLocalAudio(): LocalAudio? {
        try {
            val contentId = getColumnIndex(MediaStore.Audio.AudioColumns._ID).let(::getLong)
            val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentId)
            val data = getColumnIndex(MediaStore.DownloadColumns.DATA).let(::getString)
            if(!File(data).exists()) {
                return null
            }
            return LocalAudio(
                id = contentId.toString(),
                path = data,
                name = getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME).let(::getString),
                duration = getColumnIndex(MediaStore.Audio.AudioColumns.DURATION).let(::getLong),
                size = getColumnIndex(MediaStore.Audio.AudioColumns.SIZE).let(::getLong),
                uri = uri,
            )
        } catch (e: FileNotFoundException) {
            return null
        }
    }

}