package com.linc.audiowaveform

import kotlin.math.ceil
import kotlin.math.roundToInt

internal fun <T> Iterable<T>.fillToSize(size: Int, transform: (List<T>) -> T): List<T> {
    val capacity = ceil(size.safeDiv(count())).roundToInt()
    return map { data -> List(capacity) { data } }.flatten().chunkToSize(size, transform)
}

internal fun <T> Iterable<T>.chunkToSize(size: Int, transform: (List<T>) -> T): List<T> {
    val chunkSize = count() / size
    val remainder = count() % size
    val remainderIndex = ceil(count().safeDiv(remainder)).roundToInt()
    val chunkIteration = filterIndexed { index, _ ->
        remainderIndex == 0 || index % remainderIndex != 0
    }.chunked(chunkSize, transform)
    return when (size) {
        chunkIteration.count() -> chunkIteration
        else -> chunkIteration.chunkToSize(size, transform)
    }
}

internal fun Iterable<Float>.normalize(min: Float, max: Float): List<Float> {
    return map { (max-min) * ((it - min()) safeDiv (max() - min())) + min }
}

private fun Int.safeDiv(value: Int): Float {
    return if(value == 0) return 0F else this / value.toFloat()
}

private infix fun Float.safeDiv(value: Float): Float {
    return if (value == 0f) return 0F else this / value
}