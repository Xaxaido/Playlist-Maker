package com.practicum.playlistmaker.common.widgets

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource

@Composable
fun PlayerButton(
    modifier: Modifier,
    @DrawableRes icon: Int,
    @DrawableRes activeIcon: Int? = null,
    tint: Color? = null,
    state: Boolean? = null,
    enabled:Boolean = true,
    onClick: () -> Unit
) {
    var isActive by remember { mutableStateOf(state ?: false) }
    val scale = remember { Animatable(1f) }
    var currentIcon by remember { mutableIntStateOf(icon) }

    LaunchedEffect(state) {
        state?.let { isActive = it }
    }

    LaunchedEffect(isActive) {
        scale.animateTo(
            targetValue = 0.8f,
            animationSpec = tween(
                durationMillis = 150,
                easing = FastOutSlowInEasing
            ),
        ) {
            if (value == 0.8f) {
                currentIcon = if (!isActive) icon else activeIcon ?: icon
            }
        }

        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 150,
                easing = FastOutSlowInEasing
            )
        )
    }

    Image(
        modifier = modifier.scale(scale.value)
            .clickable(enabled = enabled) {
                isActive = !isActive
                onClick()
            },
        painter = painterResource(id = currentIcon),
        colorFilter = tint?.let { ColorFilter.tint(it) },
        contentDescription = null,
    )
}