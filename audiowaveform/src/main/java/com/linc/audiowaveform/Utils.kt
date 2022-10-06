package com.linc.audiowaveform

import kotlin.math.ceil
import kotlin.math.roundToInt

internal fun chunkedToSize(
    samples: List<Float>,
    spikes: Int,
    transform: (List<Float>) -> Float
): List<Float> {
    val size = samples.count() / spikes
    val remainder = samples.count() % spikes
    val remainderIndex = ceil(samples.count().safeDiv(remainder)).roundToInt()
    val filteredSamples = samples
        .filterIndexed { index, _ -> remainderIndex == 0 || index % remainderIndex != 0 }
        .chunked(size, transform)
    return if(filteredSamples.count() != spikes) chunkedToSize(filteredSamples, spikes, transform) else filteredSamples
}

internal fun Int.safeDiv(value: Int): Float {
    return if(value == 0) return 0F else this / value.toFloat()
}