package com.linc.audiowaveform

import kotlin.math.ceil
import kotlin.math.roundToInt

internal fun <T> Iterable<T>.chunkedToSize(size: Int, transform: (List<T>) -> T): List<T> {
    val chunkSize = count() / size
    val remainder = count() % size
    val remainderIndex = ceil(count().safeDiv(remainder)).roundToInt()
    val chunkIteration = filterIndexed { index, _ ->
        remainderIndex == 0 || index % remainderIndex != 0
    }.chunked(chunkSize, transform)
    return when (size) {
        chunkIteration.count() -> chunkIteration
        else -> chunkIteration.chunkedToSize(size, transform)
    }
}

internal fun Int.safeDiv(value: Int): Float {
    return if(value == 0) return 0F else this / value.toFloat()
}