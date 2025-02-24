package com.practicum.playlistmaker.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R

private val Yandex = FontFamily(
    Font(R.font.ys_400),
    Font(R.font.ys_500, FontWeight.W500),
)

val defaultTextStyle = TextStyle(
    fontFamily = Yandex,
    platformStyle = PlatformTextStyle(
        includeFontPadding = false
    ),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    )
)

val PlaylistMakerTypography = Typography(
    titleSmall = defaultTextStyle.copy(
        fontSize = 12.sp
    ),
    titleLarge = defaultTextStyle.copy(
        fontSize = 16.sp
    ),
    headlineMedium = defaultTextStyle.copy(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    ),
)