package com.linc.audiowaveform.sample.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.linc.audiowaveform.sample.navigation.AppNavGraph
import com.linc.audiowaveform.sample.ui.theme.ComposeaudiowaveformTheme

@Composable
fun WaveformSampleApp() {
    ComposeaudiowaveformTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AppNavGraph(navHostController = rememberNavController())
        }
    }
}