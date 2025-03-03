package com.practicum.playlistmaker.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.practicum.playlistmaker.R

val LightColors = lightColorScheme(
    primary = black,
    onPrimary = white,
    primaryContainer = black,
    onPrimaryContainer = white,
    secondary = blue,
    background = white,
    onBackground = black,
    surface = greyLight,
    onSurface = white,
    tertiary = black,
)

val DarkColors = darkColorScheme(
    primary = white,
    onPrimary = black,
    primaryContainer = white,
    onPrimaryContainer = black,
    secondary = blue,
    background = black,
    onBackground = white,
    surface = white,
    onSurface = black,
    tertiary = white,
)

val LightImages = ImageResources(
    addToPlaylist = R.drawable.added_false_icon,
    addToFavorites = R.drawable.favorite_false_icon,
    addToFavoriteActives = R.drawable.favorite_true_icon,
    nothingFound = R.drawable.nothing_found,
)

val DarkImages = ImageResources(
    addToPlaylist = R.drawable.added_false_icon_dark,
    addToFavorites = R.drawable.favorite_false_icon_dark,
    addToFavoriteActives = R.drawable.favorite_true_icon_dark,
    nothingFound = R.drawable.nothing_found_dark,
)

val LocalImages = staticCompositionLocalOf { LightImages }

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val images = if (darkTheme) DarkImages else LightImages
    val systemUiController = rememberSystemUiController()

    if(darkTheme){
        systemUiController.setSystemBarsColor(
            color = black
        )
    }else{
        systemUiController.setSystemBarsColor(
            color = blue
        )
    }

    CompositionLocalProvider(LocalImages provides images) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PlaylistMakerTypography,
            content = content
        )
    }
}