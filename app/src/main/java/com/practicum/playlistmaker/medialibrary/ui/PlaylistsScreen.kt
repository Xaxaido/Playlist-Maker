package com.practicum.playlistmaker.medialibrary.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.MediaLibraryState
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.main.ui.Routes
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.ui.recycler.ItemPlaylist
import com.practicum.playlistmaker.medialibrary.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.search.ui.DarkButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsScreen(viewModel: PlaylistsViewModel = koinViewModel(), navController: NavController, gson: Gson) {

    val state by viewModel.stateFlow.collectAsState()
    val scope = rememberCoroutineScope()
    val isClickEnabled = remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.fillMaxWidth().height(dimensionResource(R.dimen.padding_small_8x)))
            DarkButton(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_4x), bottom = dimensionResource(R.dimen.padding_small)),
                stringResource(R.string.new_playlist)
            ) {
                navController.navigate("${Routes.CreatePlaylist.name}?playlistJson=null")
            }
            GridList(
                visible = state is MediaLibraryState.Content<*>,
                state = state, scope = scope,
                navController = navController,
                isClickEnabled = isClickEnabled,
                gson = gson
            )
            Progress(modifier = null, visible = state is MediaLibraryState.Loading)
            NothingToShow(modifier = null, visible = state is MediaLibraryState.Empty, stringResource(R.string.playlist_empty))
        }
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
fun GridList(
    visible: Boolean,
    state: MediaLibraryState,
    scope: CoroutineScope,
    navController: NavController,
    isClickEnabled: MutableState<Boolean>,
    gson: Gson
) {
    if (!visible) return

    val playlists = (state as MediaLibraryState.Content<Playlist>).list

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(playlists) { item ->
            ItemPlaylist(
                playlist = item,
                onClick = {
                    if (isClickEnabled.value) {
                        isClickEnabled.value = false
                        navController.navigate("${Routes.Playlist.name}/${item.id}")
                        scope.launch {
                            delay(Util.BUTTON_ENABLED_DELAY)
                            isClickEnabled.value = true
                        }
                    }
                }
            )
        }
    }
}