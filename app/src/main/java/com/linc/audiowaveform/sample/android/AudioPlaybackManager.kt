package com.linc.audiowaveform.sample.android

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Handler
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.linc.audiowaveform.sample.model.LocalAudio
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val PLAYER_POSITION_UPDATE_TIME = 1000L
    }

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    private var currentAudioUri: Uri? = null
    private var handler: Handler? = null

    fun setAudio(localAudio: LocalAudio) {
        setAudio(localAudio.uri, localAudio.displayName)
    }

    fun setAudio(uri: Uri, title: String) {
        if (currentAudioUri == uri) {
            return
        }
        currentAudioUri = uri
        val mmd = MediaMetadata.Builder()
            .setTitle(title)
            .build()
        val rmd = MediaItem.RequestMetadata.Builder()
            .setMediaUri(uri)
            .build()
        val mediaItem = MediaItem.Builder()
            .setMediaId(UUID.randomUUID().toString())
            .setMediaMetadata(mmd)
            .setRequestMetadata(rmd)
            .build()
        controller?.setMediaItem(mediaItem)
        controller?.prepare()
    }

    fun clearAudio() {
        controller?.stop()
        controller?.clearMediaItems()
    }

    fun play() {
        controller?.play()
    }

    fun pause() {
        controller?.pause()
    }

    fun initializeController() {
        controllerFuture = MediaController.Builder(
            context,
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        ).buildAsync()
        controllerFuture?.addListener({ setController() }, MoreExecutors.directExecutor())
    }

    fun releaseController() {
        controllerFuture?.let(MediaController::releaseFuture)
        controllerFuture = null
    }

    private fun setController() {
        val controller = this.controller ?: return
        controller.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                }
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                }
            }
        )
    }

}