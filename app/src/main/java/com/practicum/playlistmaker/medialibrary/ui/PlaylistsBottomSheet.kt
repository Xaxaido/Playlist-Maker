package com.practicum.playlistmaker.medialibrary.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlaylistsBottomDialogFragmentState
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.medialibrary.ui.recycler.ItemBottomSheet
import com.practicum.playlistmaker.medialibrary.ui.view_model.PlaylistsBottomSheetDialogViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.DarkButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsBottomSheet(
    visible: Boolean,
    bottomSheetState: SheetState,
    onDismissRequest: () -> Unit,
    hideBottomSheet: () -> Unit,
    addPlaylist: () -> Unit,
    track: Track
) {
    val viewModel: PlaylistsBottomSheetDialogViewModel = koinViewModel { parametersOf(track) }
    val context = LocalContext.current
    val playlistsFlow by viewModel.playlistsFlow.collectAsState()
    val isClickEnabled = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    if (!visible) return

    LaunchedEffect(Unit) {
        viewModel.addToPlaylistFlow.collect { state ->
            if (state is PlaylistsBottomDialogFragmentState.AddToPlaylist) {
                showAddToPlaylistStatus(context, state.playlistTitle, state.isAdded) { hideBottomSheet() }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = {
            Box(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_8x))
                    .width(50.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(44))
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_6x))
                    .height(52.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                text = stringResource(R.string.add_to_playlist)
            )
            DarkButton(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_8x)),
                stringResource(R.string.new_playlist)
            ) {
                addPlaylist()
            }
            LazyColumn(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_12x))
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(playlistsFlow) { item ->
                    ItemBottomSheet(
                        playlist = item,
                        onClick = {
                            if (isClickEnabled.value) {
                                isClickEnabled.value = false
                                viewModel.addToPlaylist(item)
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
    }
}

private fun showAddToPlaylistStatus(context: Context, playlistTitle: String, isAdded: Boolean, action: () -> Unit) {
    val message = if (isAdded) {
        context.getString(R.string.add_to_playlist_success)
    } else {
        context.getString(R.string.add_to_playlist_error)
    }

    if (isAdded) {
        action()
    }

    Toast.makeText(context, "$message $playlistTitle", Toast.LENGTH_SHORT).show()
}