package com.practicum.playlistmaker.medialibrary.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlaylistMenuState
import com.practicum.playlistmaker.common.resources.PlaylistState
import com.practicum.playlistmaker.common.utils.Extensions.millisToMinutes
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.widgets.textview.EllipsizeTextView
import com.practicum.playlistmaker.main.ui.Routes
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.recycler.ItemTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    navController: NavController,
    gson: Gson,
    playlistId: Int,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var playlist by remember { mutableStateOf<Playlist?>(null) }
    var durationInMinutes by remember { mutableIntStateOf(0) }
    val viewModel: PlaylistViewModel = koinViewModel { parametersOf(playlistId) }
    val playlistState by viewModel.playlistFlow.collectAsState()

    val tracksList by viewModel.tracksListFlow.collectAsState()
    val isClickEnabled = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded)
    )
    var peekHeight by remember { mutableStateOf(0.dp) }
    val showDialog = remember { mutableStateOf(false) }
    val trackId = remember { mutableLongStateOf(-1) }

    when (val list = playlistState ) {
        is PlaylistState.PlaylistInfo -> {
            playlist = list.playlist
            durationInMinutes = list.duration.millisToMinutes().toInt()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.playlistMenuFlow.collect { state ->
            when(state) {
                is PlaylistMenuState.Share -> sharePlaylist(context, state.playlist, state.tracks)
                is PlaylistMenuState.Remove -> navController.navigateUp()
            }
        }
    }

    ConfirmDialog(
        visible = showDialog.value,
        title = stringResource(R.string.remove_track_title),
        text = stringResource(R.string.remove_track_message),
        dismissButtonText = stringResource(R.string.dialog_message_no),
        confirmButtonText = stringResource(R.string.dialog_message_yes),
        onDismiss = {
            showDialog.value = false
        },
        onConfirm = {
            showDialog.value = false
            viewModel.removeTrack(trackId.longValue)
        },
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetDragHandle = {
            Box(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_8x))
                    .width(50.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(44))
                    .background(colorResource(R.color.handle_color))
            )
        },
        sheetPeekHeight = peekHeight,
        sheetContent = {
            Column(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_6x))
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NoTracks(tracksList.isEmpty())
                TracksList(
                    visible = tracksList.isNotEmpty(),
                    navController = navController,
                    scope = scope,
                    isClickEnabled = isClickEnabled,
                    tracksList = tracksList,
                    trackId = trackId,
                    showDialog = showDialog
                )
            }
        }
    ) {
        PlaylistsBottomSheet(
            visible = showBottomSheet,
            gson = gson,
            navController = navController,
            viewModel = viewModel,
            bottomSheetState = bottomSheetState,
            onDismissRequest = { showBottomSheet = false }
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = playlist?.cover?.takeIf { it.isNotEmpty() }?.let {
                        rememberAsyncImagePainter(model = it)
                    } ?: painterResource(R.drawable.album_cover_stub),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.TopStart)
                        .aspectRatio(1f)
                )
                Image(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.padding_small_10x),
                        top = dimensionResource(R.dimen.padding_small_12x)
                    )
                        .size(16.dp)
                        .clickable { navController.navigateUp() }
                )
            }
            Box(
                Modifier.fillMaxSize()
                    .background(colorResource(R.color.greyLight))
            ) {
                Column(
                    Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small_8x))
                ) {
                    TextBox(
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_12x)),
                        text = playlist?.name,
                        isCaption = true
                    )
                    TextBox(
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_2x)),
                        text = playlist?.description,
                    )
                    AndroidView(
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_2x))
                            .fillMaxWidth(),
                        factory = { context ->
                            EllipsizeTextView(
                                ContextThemeWrapper(
                                    context,
                                    R.style.PlaylistDataText
                                )
                            )
                        },
                        update = { view ->
                            val count = Util.formatValue(
                                playlist?.tracksCount ?: 0,
                                context.getString(R.string.playlist_track)
                            )

                            view.setText(
                                Util.formatValue(
                                    durationInMinutes,
                                    context.getString(R.string.playlist_minute)
                                ),
                                count
                            )
                        }
                    )

                    Row(
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_12x))
                            .onGloballyPositioned { coordinates ->
                                peekHeight = with(density) {
                                    screenHeight - coordinates.boundsInRoot().bottom.toDp() - context.resources.getDimension(R.dimen.padding_small_12x).toDp()
                                }
                            }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_share),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                                .clickable { viewModel.sharePlaylist() }
                        )
                        Image(
                            painter = painterResource(R.drawable.ic_menu),
                            contentDescription = null,
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_small_12x))
                                .size(24.dp)
                                .clickable { showBottomSheet = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TextBox(
    modifier: Modifier,
    text: String?,
    isCaption: Boolean = false
) {
    if (text == null) return

    Text(
        text = text,
        style = if (isCaption) MaterialTheme.typography.displayLarge else MaterialTheme.typography.displayMedium,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun TracksList(
    visible: Boolean,
    navController: NavController,
    scope: CoroutineScope,
    isClickEnabled: MutableState<Boolean>,
    tracksList: List<Track>,
    trackId: MutableLongState,
    showDialog: MutableState<Boolean>
) {
    if (!visible) return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tracksList) { item ->
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
                },
                onLongClick = {
                    trackId.longValue = item.id
                    showDialog.value = true
                }
            )
        }
    }
}

@Composable
fun NoTracks(
    visible: Boolean
) {
    if (!visible) return

    Text(
        modifier = Modifier.height(52.dp)
            .wrapContentHeight(Alignment.CenterVertically),
        style = MaterialTheme.typography.headlineLarge.copy(
            color = MaterialTheme.colorScheme.onBackground
        ),
        text = stringResource(R.string.playlist_empty)
    )
}

private fun sharePlaylist(context: Context,playlist: Playlist, list: List<Track>) {
    if (list.isEmpty()) {
        Toast.makeText(context, context.getString(R.string.share_playlist_error), Toast.LENGTH_SHORT).show()
        return
    }

    val message = buildString {
        appendLine(playlist.name)
        appendLine(playlist.description)
        appendLine(Util.formatValue(playlist.tracksCount, context.getString(R.string.playlist_track)))
        appendLine()
        list.forEachIndexed { index, track ->
            appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.duration.millisToSeconds()})")
        }
    }

    Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
        context.startActivity(Intent.createChooser(this, null))
    }
}