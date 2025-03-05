package com.practicum.playlistmaker.main.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.theme.PlaylistMakerTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(gson: Gson) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = koinViewModel()
    val darkTheme by viewModel.darkMode.collectAsState()
    var themeState by remember { mutableStateOf(false) }
    val onBackClick = remember { mutableStateOf<(() -> Unit)?>(null) }
    var topBarTitle by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getThemeSettings().also { theme ->
            themeState = Util.getTheme(context, theme.themeName)
        }
    }

    PlaylistMakerTheme(darkTheme = darkTheme ?: themeState) {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        val screensWithoutBackButton = listOf(Routes.Settings.name, Routes.MediaLibrary.name, Routes.Search.name)
        val screensWithoutTopBar = listOf(Routes.Playlist.name)
        val showBackButton = currentDestination?.route !in screensWithoutBackButton
        val showBottomBar = currentDestination?.route in screensWithoutBackButton
        val showTopBar = screensWithoutTopBar.any {
            currentDestination?.route?.startsWith(it) == false
        }

        val screensWithTitle = listOf(
            Routes.Settings.name,
            Routes.MediaLibrary.name,
            Routes.Search.name,
            Routes.CreatePlaylist.name
        )
        val showTitle = screensWithTitle.any { currentDestination?.route?.startsWith(it) == true }

        LaunchedEffect(currentDestination) {
            topBarTitle = if (currentDestination?.route?.startsWith(Routes.CreatePlaylist.name) == true) {
                navController.currentBackStackEntry?.arguments?.getString("playlistJson")?.let {
                    context.getString(R.string.edit_playlist)
                } ?: context.getString(R.string.new_playlist)
            } else {
                getScreenTitle(context, currentDestination?.route)
            }
        }

        Scaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.onBackground),
            topBar = {
                if (showTopBar) {
                    TopAppBar(
                        title = {
                            if (showTitle) {
                                Text(
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        color = MaterialTheme.colorScheme.onBackground
                                    ),
                                    text = topBarTitle
                                )
                            }
                        },
                        navigationIcon = {
                            if (showBackButton) {
                                IconButton(onClick = onBackClick.value?.let { { it() } }
                                    ?: { navController.navigateUp() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        contentDescription = "Назад"
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            },
            bottomBar = {
                if (showBottomBar) BottomNavigationBar(navController)
            }
        ) { innerPadding ->
            NavGraph(viewModel = viewModel, navController = navController, modifier = Modifier.padding(innerPadding), gson = gson, onBackClick = onBackClick)
        }
    }
}

fun getScreenTitle(context: Context, route: String?): String {
    return when {
        route?.startsWith(Routes.Settings.name) == true -> context.getString(R.string.settings)
        route?.startsWith(Routes.MediaLibrary.name) == true -> context.getString(R.string.media_library)
        route?.startsWith(Routes.Search.name) == true -> context.getString(R.string.search)
        else -> ""
    }
}