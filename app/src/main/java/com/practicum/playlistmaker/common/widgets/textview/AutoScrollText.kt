package com.practicum.playlistmaker.common.widgets.textview

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AutoScrollText(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: String,
    style: TextStyle,
    scrollDelay: Long = 2000L,
) {
    val density = LocalDensity.current
    var textWidth by remember { mutableFloatStateOf(0f) }
    var paint by remember { mutableStateOf(Paint()) }
    val gap = with(density) { 100.dp.toPx() }
    val animationState = remember { mutableStateOf(0f) }

    LaunchedEffect(text) {
        paint.asFrameworkPaint().apply {
            color = style.color.toArgb()
            textSize = with(density) { style.fontSize.toPx() }
            isAntiAlias = true
        }
        textWidth = paint.asFrameworkPaint().measureText(text)
    }

    LaunchedEffect(Unit) {
        paint.asFrameworkPaint().apply {
            color = style.color.toArgb()
            textSize = with(density) { style.fontSize.toPx() }
            isAntiAlias = true
        }
        textWidth = paint.asFrameworkPaint().measureText(text)

        while (true) {
            delay(scrollDelay)

            val target = -(textWidth + gap)
            val duration = ((textWidth + gap) / 100 * 1000).toInt()

            animate(
                initialValue = 0f,
                targetValue = target,
                animationSpec = tween(
                    durationMillis = duration,
                    easing = LinearEasing,
                ),
            ) { value, _ ->
                animationState.value = value
            }

            delay(scrollDelay)
        }
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().height(24.dp)) {
            val offsetX = animationState.value

            drawIntoCanvas {
                val nativeCanvas = it.nativeCanvas
                nativeCanvas.drawText(text, offsetX, 0f, paint.asFrameworkPaint())
                nativeCanvas.drawText(text, offsetX + textWidth + gap, 0f, paint.asFrameworkPaint())
            }
        }
    }
}