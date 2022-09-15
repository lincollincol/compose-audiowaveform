package com.linc.audiowaveform.sample.ui.screen.waveform.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.linc.audiowaveform.infiniteHorizontalGradient
import com.linc.audiowaveform.infiniteLinearGradient
import com.linc.audiowaveform.infiniteVerticalGradient
import java.util.*

data class ColorPalette(
    val label: String,
    val progressColor: Brush,
    val waveformColor: Brush
)

@JvmName("getPalettesFun")
@Composable
fun getMockPalettes() = listOf(
    ColorPalette("Cyan/Gray", SolidColor(Color.Cyan), SolidColor(Color.LightGray)),
    ColorPalette("Blue/Yellow", SolidColor(Color.Blue), SolidColor(Color.Yellow)),
    ColorPalette("White/Red", SolidColor(Color.White), SolidColor(Color.Red)),
    ColorPalette(
        "ComposeColors",
        Brush.horizontalGradient(listOf(Color(0xFF136FC3), Color(0xFF76EF66))),
        SolidColor(Color.LightGray)
    ),
    ColorPalette(
        "Frozen",
        Brush.verticalGradient(listOf(Color(0xFF403B4A), Color(0xFFE7E9BB))),
        SolidColor(Color.LightGray)
    ),
    ColorPalette(
        "IntuitivePurple",
        Brush.linearGradient(listOf(Color(0xFFDA22FF), Color(0xFF9733EE))),
        SolidColor(Color.LightGray)
    ),
    ColorPalette(
        "IntuitivePink",
        Brush.infiniteHorizontalGradient(
            colors = listOf(Color(0xFFFC8B6A), Color(0xFFE10467)),
            width = 192.dp.value
        ),
        SolidColor(Color.LightGray)
    ),
    ColorPalette(
        "Ver",
        Brush.infiniteVerticalGradient(
            colors = listOf(Color(0xFFFFE000), Color(0xFF799F0C)),
            width = 96.dp.value
        ),
        SolidColor(Color.LightGray)
    ),
    ColorPalette(
        "NightFade",
        Brush.infiniteLinearGradient(
            colors = listOf(Color(0xFFA18CD1), Color(0xFFFBC2EB)),
            width = 48.dp.value
        ),
        SolidColor(Color.LightGray)
    ),
)