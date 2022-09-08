package com.linc.audiowaveform.sample.extension

import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.log2
import kotlin.math.pow

val File.formatSize: String
    get() = length().formatAsFileSize

val Int.formatAsFileSize: String
    get() = toLong().formatAsFileSize

val Long.formatAsFileSize: String
    get() = log2(if (this != 0L) toDouble() else 1.0).toInt().div(10).let {
        val precision = when (it) {
            0 -> 0; 1 -> 1; else -> 2
        }
        val prefix = arrayOf("", "K", "M", "G", "T", "P", "E", "Z", "Y")
        String.format("%.${precision}f ${prefix[it]}B", toDouble() / 2.0.pow(it * 10.0))
    }

val Long.formatAsAudioDuration: String get() = TimeUnit.MILLISECONDS.let { timeUnit ->
    String.format(
        "%d:%d",
        timeUnit.toMinutes(this),
        timeUnit.toSeconds(this).let { if(it < 100) it else it / 10 }
    )
}