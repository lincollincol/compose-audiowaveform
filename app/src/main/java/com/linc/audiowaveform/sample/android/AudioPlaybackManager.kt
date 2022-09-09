package com.linc.audiowaveform.sample.android

import android.content.ComponentName
import android.content.Context
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.linc.audiowaveform.sample.model.LocalAudio
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

class AudioPlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val PLAYER_POSITION_UPDATE_TIME = 500L
    }

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    private var currentMediaItem: MediaItem? = null
    private var handler: Handler? = null
    val events: MutableSharedFlow<Event> = MutableSharedFlow()

    fun setAudio(localAudio: LocalAudio) {
        setAudio(localAudio.uri, localAudio.displayName)
    }

    fun setAudio(uri: Uri, title: String) {
        setAudio(getMediaItem(uri, title))
    }

    fun setAudio(mediaItem: MediaItem) {
        val controllerItemId = controller?.currentMediaItem?.mediaId
        currentMediaItem = mediaItem
        if(controllerFuture?.isDone == false || controllerItemId == mediaItem.mediaId) {
            return
        }
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

    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }

    fun initializeController() {
        controllerFuture = MediaController.Builder(
            context,
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        ).buildAsync()
        controllerFuture?.addListener(::onControllerCreated, MoreExecutors.directExecutor())
    }

    fun releaseController() {
        controllerFuture?.let(MediaController::releaseFuture)
        controllerFuture = null
    }

    private fun getMediaItem(uri: Uri, title: String): MediaItem {
        val mmd = MediaMetadata.Builder()
            .setTitle(title)
            .build()
        val rmd = MediaItem.RequestMetadata.Builder()
            .setMediaUri(uri)
            .build()
        return MediaItem.Builder()
            .setMediaId(UUID.randomUUID().toString())
            .setMediaMetadata(mmd)
            .setRequestMetadata(rmd)
            .build()
    }

    private fun onControllerCreated() {
        val controller = this.controller ?: return
        currentMediaItem?.let(::setAudio)
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed(
            object : Runnable {
                override fun run() {
                    val playbackPosition = controller.currentPosition
                    sendEvent(Event.PositionChanged(playbackPosition))
                    handler?.postDelayed(this, PLAYER_POSITION_UPDATE_TIME)
                }
            },
            PLAYER_POSITION_UPDATE_TIME
        )
        controller.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    sendEvent(Event.PlayingChanged(isPlaying))
                }
            }
        )
    }

    private fun sendEvent(event: Event) {
        runBlocking { events.emit(event) }
    }

    sealed interface Event {
        data class PositionChanged(val position: Long) : Event
        data class PlayingChanged(val isPlaying: Boolean) : Event
    }

}