package com.practicum.playlistmaker.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.practicum.playlistmaker.medialibrary.ui.CreatePlaylist
import com.practicum.playlistmaker.medialibrary.ui.MediaLibraryScreen
import com.practicum.playlistmaker.medialibrary.ui.PlaylistScreen
import com.practicum.playlistmaker.player.ui.PlayerScreen
import com.practicum.playlistmaker.search.ui.SearchScreen
import com.practicum.playlistmaker.settings.ui.SettingsScreen
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    viewModel: SettingsViewModel,
    navController: NavHostController,
    startDestination: String = Routes.MediaLibrary.name,
    modifier: Modifier,
    gson: Gson,
    onBackClick: MutableState<(() -> Unit)?>
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(
            route = Routes.Settings.name
        ) {
            SettingsScreen(viewModel)
        }
        composable(
            route = Routes.MediaLibrary.name
        ) {
            MediaLibraryScreen(navController = navController, gson = gson)
        }
        composable(
            route = Routes.Search.name
        ) {
            SearchScreen(navController)
        }

        composable(
            route = "${Routes.Player.name}/{trackJson}",
            arguments = listOf(navArgument("trackJson") { type = NavType.StringType })
        ) {  navBackStackEntry ->
            val trackJson = navBackStackEntry.arguments?.getString("trackJson").orEmpty()
            val decodedJson = URLDecoder.decode(trackJson, StandardCharsets.UTF_8.toString())
            PlayerScreen(navController = navController, trackJson = decodedJson)
        }

        composable(
            route = "${Routes.CreatePlaylist.name}?playlistJson={playlistJson}",
            arguments = listOf(
                navArgument("playlistJson") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {  navBackStackEntry ->
            val playlistJson = navBackStackEntry.arguments?.getString("playlistJson")
            var decodedJson: String? = null

            playlistJson?.let {
                decodedJson = URLDecoder.decode(playlistJson, StandardCharsets.UTF_8.toString())
            }
            CreatePlaylist(navController = navController, gson = gson, playlistJson = decodedJson, onBackClick = onBackClick)
        }

        composable(
            route = "${Routes.Playlist.name}/{playlistId}",
            arguments = listOf(
                navArgument("playlistId") { type = NavType.IntType }
            )
        ) {  navBackStackEntry ->
            val playlistId = navBackStackEntry.arguments?.getInt("playlistId") ?: -1
            PlaylistScreen(navController = navController, gson = gson, playlistId = playlistId)
        }
    }
}