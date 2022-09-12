package com.linc.audiowaveform.sample.android

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.linc.audiowaveform.sample.model.LocalAudio
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

class AudioPlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context
) : Player.Listener {

    companion object {
        private const val PLAYER_POSITION_UPDATE_TIME = 500L
    }

    val events: MutableSharedFlow<Event> = MutableSharedFlow()

    private var lastEmittedPosition: Long = 0
    private var currentMediaItem: MediaItem? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    private var handler: Handler? = null

    private val playerPositionRunnable = object : Runnable {
        override fun run() {
            val playbackPosition = controller?.currentPosition ?: 0
            // Emit only new player position
            if(playbackPosition != lastEmittedPosition) {
                sendEvent(Event.PositionChanged(playbackPosition))
                lastEmittedPosition = playbackPosition
            }
            handler?.postDelayed(this, PLAYER_POSITION_UPDATE_TIME)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        sendEvent(Event.PlayingChanged(isPlaying))
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        if(playbackState == Player.STATE_ENDED) {
            controller?.pause()
            controller?.seekTo(0)
        }
    }

    fun setAudio(localAudio: LocalAudio) {
        setAudio(localAudio.uri, localAudio.nameWithoutExtension)
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
        currentMediaItem = null
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
        clearAudio()
        controller?.removeListener(this)
        controllerFuture?.let(MediaController::releaseFuture)
        controllerFuture = null
        handler?.removeCallbacks(playerPositionRunnable)
        handler = null
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
        currentMediaItem?.let(::setAudio)
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed(playerPositionRunnable, PLAYER_POSITION_UPDATE_TIME)
        controller?.addListener(this)
    }

    private fun sendEvent(event: Event) {
        runBlocking { events.emit(event) }
    }

    sealed interface Event {
        data class PositionChanged(val position: Long) : Event
        data class PlayingChanged(val isPlaying: Boolean) : Event
    }

}