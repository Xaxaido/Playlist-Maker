package com.practicum.playlistmaker.main.ui

import androidx.compose.ui.graphics.painter.Painter

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: Painter,
)