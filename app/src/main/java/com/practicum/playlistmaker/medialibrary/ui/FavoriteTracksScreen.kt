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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.MediaLibraryState
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.main.ui.Routes
import com.practicum.playlistmaker.medialibrary.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.recycler.ItemTrack
import com.practicum.playlistmaker.theme.LocalImages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
            Progress(modifier = null, visible = state is MediaLibraryState.Loading)
            NothingToShow(modifier = null, visible = state is MediaLibraryState.Empty, stringResource(R.string.empty_medialibrary))
        }
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
                onClick = {
                    if (isClickEnabled.value) {
                        isClickEnabled.value = false
                        val json = Util.trackToJson(item)
                        val encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
                        navController.navigate("${Routes.Player.name}/$encodedJson")
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
fun Progress(modifier: Modifier?, visible: Boolean) {
    if (!visible) return

    Box(
       modifier = modifier ?: Modifier.padding(top = dimensionResource(R.dimen.ui_margin_top))
           .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(44.dp),
            color = colorResource(R.color.blue)
        )
    }
}

@Composable
fun NothingToShow(modifier: Modifier?, visible: Boolean, message: String) {
    if (!visible) return

    Column(
        modifier = modifier ?: Modifier.fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.ui_margin_top)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            alignment = Alignment.Center,
            painter = painterResource(LocalImages.current.nothingFound),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small_6x)),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            text = message
        )
    }
}