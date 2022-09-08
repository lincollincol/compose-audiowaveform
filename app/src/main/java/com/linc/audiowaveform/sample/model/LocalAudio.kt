package com.linc.audiowaveform.sample.model

import android.net.Uri

data class LocalAudio(
    val id: String,
    val uri: Uri,
    val displayName: String,
    val duration: Long,
    val size: Long
)