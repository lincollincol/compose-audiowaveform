package com.linc.audiowaveform.sample.android

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Build
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PriorityTaskManager
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.linc.audiowaveform.sample.MainActivity

class PlaybackService : MediaSessionService() {

    private val librarySessionCallback = CustomMediaLibrarySessionCallback()
    private var player: ExoPlayer? = null
    private var mediaLibrarySession: MediaSession? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaLibrarySession
    }

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
        val sessionActivityPendingIntent = TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@PlaybackService, MainActivity::class.java))
            val immutableFlag = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> PendingIntent.FLAG_IMMUTABLE
                else -> 0
            }
            getPendingIntent(0, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        mediaLibrarySession = MediaSession.Builder(this, player!!)
            .setCallback(librarySessionCallback)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
    }

    private inner class CustomMediaLibrarySessionCallback : MediaSession.Callback {
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            val updatedMediaItems = mediaItems.map { mediaItem ->
                mediaItem.buildUpon()
                    .setUri(mediaItem.requestMetadata.mediaUri)
                    .build()
            }
            return Futures.immediateFuture(updatedMediaItems)
        }
    }
}