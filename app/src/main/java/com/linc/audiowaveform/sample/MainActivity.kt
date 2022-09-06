package com.linc.audiowaveform.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.sample.ui.theme.ComposeaudiowaveformTheme
import linc.com.amplituda.Amplituda
import linc.com.amplituda.Cache

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val amplituda = Amplituda(this)
        amplituda.setLogConfig(Log.ERROR, true)
        val amplitudes = amplituda.processAudio(
            "/storage/9016-4EF8/MUSIC/Palace - Heaven Up There.mp3",
            Cache.withParams(Cache.REUSE)
        ).get().amplitudesAsList()

        setContent {
            ComposeaudiowaveformTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    var waveformProgress by rememberSaveable { mutableStateOf(0F) }
                    AudioWaveform(
                        progress = waveformProgress,
                        samples = amplitudes
                    ) {
                        waveformProgress = it
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeaudiowaveformTheme {
        Greeting("Android")
    }
}