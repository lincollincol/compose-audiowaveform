package com.linc.audiowaveform.sample.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.linc.audiowaveform.sample.model.LocalAudio
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.Cache
import javax.inject.Inject


class LocalMediaDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val amplituda: Amplituda,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val contentResolver: ContentResolver get() = context.contentResolver

    suspend fun loadAudioAmplitudes(uri: Uri): List<Int> {
        return contentResolver.openInputStream(uri)?.use { stream ->
            amplituda.processAudio(stream, Cache.withParams(Cache.REUSE)).get().amplitudesAsList()
        } ?: emptyList()
    }

    suspend fun loadAudioById(id: String): LocalAudio? = withContext(ioDispatcher) {
        val mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
        )
        val selection = MediaStore.Audio.Media._ID + "=?"
        val selectionArgs = arrayOf(id)
        val cursor = contentResolver.query(mediaContentUri, projection, selection, selectionArgs, null)
        return@withContext cursor?.use {
            if (it.moveToFirst()) it.toLocalAudio() else null
        }
    }

    suspend fun loadAudioFiles() : List<LocalAudio> = withContext(ioDispatcher) {
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val content = mutableListOf<LocalAudio>()
        val cursor = contentResolver.query(
            contentUri,
            arrayOf(
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATE_ADDED,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.SIZE
            ),
            null,
            null,
            "${MediaStore.MediaColumns.DATE_ADDED} DESC"
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

    private fun Cursor.toLocalAudio(): LocalAudio {
        val contentId = getColumnIndex(MediaStore.Audio.AudioColumns._ID).let(::getLong)
        return LocalAudio(
            id = contentId.toString(),
            displayName = getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME).let(::getString),
            duration = getColumnIndex(MediaStore.Audio.AudioColumns.DURATION).let(::getLong),
            size = getColumnIndex(MediaStore.Audio.AudioColumns.SIZE).let(::getLong),
            uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentId),
        )
    }
}