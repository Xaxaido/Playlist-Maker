package com.practicum.playlistmaker.medialibrary.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.MediaLibraryState
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.widgets.BlurredImageView
import com.practicum.playlistmaker.common.widgets.recycler.ParticleView
import com.practicum.playlistmaker.medialibrary.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.recycler.ItemTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteTracksScreen(viewModel: FavoriteTracksViewModel = koinViewModel(), navController: NavController) {

    val state by viewModel.stateFlow.collectAsState()
    val scope = rememberCoroutineScope()
    val isClickEnabled = remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            Spacer(Modifier.fillMaxWidth().height(dimensionResource(R.dimen.padding_small_8x)))
            List(visible = state is MediaLibraryState.Content<*>, state = state, scope = scope, navController = navController, isClickEnabled = isClickEnabled)
            Progress(visible = state is MediaLibraryState.Loading)
            EmptyMediaLibrary(visible = state is MediaLibraryState.Empty, stringResource(R.string.empty_medialibrary))
        }
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context  ->
                ParticleView(context)
            }
        )
        AndroidView(
            modifier = Modifier.fillMaxWidth()
                .height(dimensionResource(R.dimen.toolbar_height)),
            factory = { context  ->
                BlurredImageView(context)
            }
        )
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
fun List(visible: Boolean, state: MediaLibraryState, scope: CoroutineScope, navController: NavController, isClickEnabled: MutableState<Boolean>) {
    if (!visible) return

    val tracks = (state as MediaLibraryState.Content<Track>).list

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tracks) { item ->
            ItemTrack(
                track = item,
                onClick =  {
                    if (isClickEnabled.value) {
                        isClickEnabled.value = false
                        val json = Util.trackToJson(item)
                        navController.navigate(
                            R.id.action_send_to_player,
                            PlayerFragment.createArgs(json),
                        )
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

@Composable
fun Progress(visible: Boolean) {
    if (!visible) return

    CircularProgressIndicator()
}

@Composable
fun EmptyMediaLibrary(visible: Boolean, message: String) {
    if (!visible) return

    Column(
        Modifier.fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.ui_margin_top)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            alignment = Alignment.Center,
            painter = painterResource(id = R.drawable.nothing_found),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small_6x)),
            style = TextStyle(
                fontSize = dimensionResource(id = R.dimen.text_big).value.sp,
                color = colorResource(R.color.textButton_TextColor),
            ),
            text = message
        )
    }
}