package com.practicum.playlistmaker.common.widgets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SlidingTime(
    modifier: Modifier = Modifier,
    time: String,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    textSize: TextUnit = 14.sp
) {
    var currentTime by remember { mutableStateOf("") }
    var newTime = if (time.isEmpty()) stringResource(R.string.default_duration_start) else time

    LaunchedEffect(newTime) {
        if (newTime != currentTime ) {
            currentTime = newTime
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        newTime.forEachIndexed { _ , newChar ->
            Box(contentAlignment = Alignment.Center) {
                AnimatedContent(
                    targetState = newChar,
                    transitionSpec = {
                        slideInVertically { -it } + fadeIn() with slideOutVertically { it } + fadeOut()
                    },
                    label = "DigitAnimation"
                ) { animatedChar ->
                    Text(
                        text = animatedChar.toString(),
                        fontSize = textSize,
                        color = textColor,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}