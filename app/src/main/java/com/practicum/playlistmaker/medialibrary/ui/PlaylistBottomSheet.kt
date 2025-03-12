package com.practicum.playlistmaker.medialibrary.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlaylistState
import com.practicum.playlistmaker.main.ui.Routes
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.ui.recycler.ItemBottomSheet
import com.practicum.playlistmaker.medialibrary.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.settings.ui.CompoundButton
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsBottomSheet(
    visible: Boolean,
    gson: Gson,
    navController: NavController,
    viewModel: PlaylistViewModel,
    bottomSheetState: SheetState,
    onDismissRequest: () -> Unit,
) {
    if (!visible) return

    val playlistState by viewModel.playlistFlow.collectAsState()
    var playlist by remember { mutableStateOf<Playlist?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(playlistState) {
        when (val state = playlistState) {
            is PlaylistState.PlaylistInfo -> {
                playlist = state.playlist
            }
        }
    }

    if (showDialog) {
        ConfirmDialog(
            visible = showDialog,
            title = stringResource(R.string.remove_playlist),
            text = stringResource(R.string.remove_playlist_message),
            dismissButtonText = stringResource(R.string.dialog_message_no),
            confirmButtonText = stringResource(R.string.dialog_message_yes),
            onDismiss = {
                showDialog = false
            },
            onConfirm = {
                showDialog = false
                viewModel.removePlaylist()
            },
        )
    }

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = {
            Box(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_8x), bottom = dimensionResource(R.dimen.padding_small_6x))
                    .width(50.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(44))
                    .background(colorResource(R.color.handle_color))
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            playlist?.let {
                ItemBottomSheet(
                    playlist = it
                ) {}
            }
            CompoundButton(
                title = stringResource(R.string.share_playlist)
            ) {
                onDismissRequest()
                viewModel.sharePlaylist()
            }
            CompoundButton(
                title = stringResource(R.string.edit_playlist)
            ) {
                val json = gson.toJson(playlist)
                val encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
                navController.navigate("${Routes.CreatePlaylist.name}?playlistJson=$encodedJson")
            }
            CompoundButton(
                title = stringResource(R.string.remove_playlist)
            ) { showDialog = true }
        }
    }
}