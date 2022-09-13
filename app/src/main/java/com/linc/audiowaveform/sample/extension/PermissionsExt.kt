package com.linc.audiowaveform.sample.extension

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.linc.audiowaveform.sample.BuildConfig

/**
 * Check for android 13 (TIRAMISU) permissions
 */
fun getReadStoragePermission() = when {
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ->
        Manifest.permission.READ_EXTERNAL_STORAGE
    else -> Manifest.permission.READ_MEDIA_AUDIO
}