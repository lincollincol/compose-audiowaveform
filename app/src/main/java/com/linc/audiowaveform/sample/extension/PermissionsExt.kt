package com.linc.audiowaveform.sample.extension

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

fun isPermissionGranted(
    context: Context,
    permission: String
) = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
