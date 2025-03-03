package com.practicum.playlistmaker.player.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.PlayerState
import com.practicum.playlistmaker.common.utils.Extensions.dpToPx
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.common.utils.Util.ARGS_TRACK
import com.practicum.playlistmaker.common.widgets.PlayerButton
import com.practicum.playlistmaker.common.widgets.SlidingTime
import com.practicum.playlistmaker.common.widgets.textview.AutoScrollTextView
import com.practicum.playlistmaker.medialibrary.ui.PlaylistsBottomSheet
import com.practicum.playlistmaker.player.services.MusicService
import com.practicum.playlistmaker.player.ui.PlayerConstants.PLAYER_SCREEN
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.theme.LocalImages
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

object PlayerConstants {
    const val PLAYER_SCREEN = "PlayerScreen"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(navController: NavController, trackJson: String) {
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                Log.d(PLAYER_SCREEN, "Permission granted for POST_NOTIFICATIONS")
            } else {
                Log.d(PLAYER_SCREEN, "Permission denied for POST_NOTIFICATIONS")
            }
        }
    )

    val images = LocalImages.current
    val bottomSheetState = rememberModalBottomSheetState()
    var shouldShowBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val viewModel: PlayerViewModel = koinViewModel { parametersOf(trackJson) }
    val serviceConnection = remember { MusicServiceConnection(viewModel) }
    var track by remember { mutableStateOf<Track?>(null) }
    var time by remember { mutableStateOf("") }
    var isStopped by remember { mutableStateOf<Boolean?>(null) }
    var cover by remember { mutableStateOf<Bitmap?>(null) }
    var trackLoaded = remember { mutableStateOf(false) }

    val playerState by viewModel.playerState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val trackDescription by viewModel.trackDescription.collectAsState()
    val progress by viewModel.trackBufferingState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setTrack()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    when (val state = playerState) {
        is PlayerState.TrackData -> {
            track = state.track
            track?.let {
                viewModel.searchTrackDescription(it.artistViewUrl)
            }
        }
        is PlayerState.CurrentTime -> {
            time = state.time
        }
        is PlayerState.Playing -> {
            isStopped = false
        }
        is PlayerState.Stop -> {
            isStopped = true
            time = context.getString(R.string.default_duration_start)
        }
    }

    DisposableEffect(Unit) {
        val intent = Intent(context, MusicService::class.java).apply {
            putExtra(ARGS_TRACK, trackJson)
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        onDispose {
            context.unbindService(serviceConnection)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val onResumeObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.showNotification(false)
            }
        }

        val onPauseObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        cover?.let { viewModel.showNotification(true, it) }
                    }
                } else {
                    cover?.let { viewModel.showNotification(true, it) }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(onResumeObserver)
        lifecycleOwner.lifecycle.addObserver(onPauseObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(onResumeObserver)
            lifecycleOwner.lifecycle.removeObserver(onPauseObserver)
        }
    }

    PlaylistsBottomSheet(
        visible = shouldShowBottomSheet,
        bottomSheetState = bottomSheetState,
        onDismissRequest = { shouldShowBottomSheet = false },
        hideBottomSheet = {
            scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                if (!bottomSheetState.isVisible) {
                    shouldShowBottomSheet = false
                }
            }
        },
        addPlaylist = {
            /*navController.navigate(
                R.id.action_create_playlist,
                CreatePlaylistFragment.createArgs(null),
            )*/
        },
        track = viewModel.track
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (
                albumCover, trackTitle, artistName, addToPlaylist, showProgress, play, addToFavorites, currentTime
            ) = createRefs()
            val (
                playerDuration, playerDurationText, albumTitle, albumTitleText, year, yearText,
                genre, genreText, country, countryText
            ) = createRefs()
            val guidelineLeft = createGuidelineFromStart(dimensionResource(R.dimen.padding_small_8x))
            val guidelineRight = createGuidelineFromEnd(dimensionResource(R.dimen.padding_small_8x))
            val barrierEnd = createEndBarrier(playerDuration)

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(track?.getPlayerAlbumCover())
                    .crossfade(true)
                    .transformations(RoundedCornersTransformation(8.dpToPx(context).toFloat()))
                    .build(),
                placeholder = painterResource(R.drawable.album_cover_stub),
                fallback = painterResource(R.drawable.album_cover_stub),
                onSuccess = {
                    cover = (it.result.drawable as? BitmapDrawable)?.bitmap
                },
                contentDescription = null,
                modifier = Modifier.aspectRatio(1f)
                    .padding(horizontal = dimensionResource(R.dimen.padding_small_4x))
                    .padding(top = dimensionResource(R.dimen.padding_small_13x))
                        .constrainAs(albumCover) {
                            start.linkTo(guidelineLeft)
                            end.linkTo(guidelineRight)
                        }
                    .padding(horizontal = dimensionResource(R.dimen.padding_small_4x)),
            )
            CustomText(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_12x))
                    .constrainAs(trackTitle) {
                        start.linkTo(parent.start)
                        end.linkTo(guidelineRight)
                        top.linkTo(albumCover.bottom)
                        bottom.linkTo(artistName.top)
                    },
                text = track?.trackName.toString(),
                size = dimensionResource(R.dimen.title_text_size).value
            )
            CustomText(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_6x))
                    .constrainAs(artistName) {
                        start.linkTo(parent.start)
                        end.linkTo(guidelineRight)
                        top.linkTo(trackTitle.bottom)
                        bottom.linkTo(play.top)
                    },
                text = track?.artistName.toString(),
                size = dimensionResource(R.dimen.text_regular).value
            )
            PlayerButton(
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_small_4x), top = dimensionResource(R.dimen.padding_small_15x))
                    .size(dimensionResource(R.dimen.play_btn_small))
                    .constrainAs(addToPlaylist) {
                        start.linkTo(guidelineLeft)
                        top.linkTo(play.top)
                        bottom.linkTo(play.bottom)
                        verticalBias = .5f
                    },
                    icon = images.addToPlaylist
            ) {
                shouldShowBottomSheet = true
                scope.launch { bottomSheetState.partialExpand() }
            }
            Progress(
                loaded = trackLoaded,
                modifier = Modifier.constrainAs(showProgress) {
                    start.linkTo(play.start)
                    end.linkTo(play.end)
                    top.linkTo(play.top)
                    bottom.linkTo(play.bottom)
                },
                progress = progress.toFloat()
            )
            PlayerButton(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_15x))
                    .size(dimensionResource(R.dimen.play_btn_big))
                    .alpha(if (progress >= 100f) 1f else .5f)
                    .constrainAs(play) {
                        start.linkTo(addToPlaylist.end)
                        end.linkTo(addToFavorites.start)
                        top.linkTo(artistName.bottom)
                        bottom.linkTo(currentTime.top)
                        horizontalBias = .5f
                    },
                icon = R.drawable.play_button,
                activeIcon = R.drawable.pause_button,
                tint = MaterialTheme.colorScheme.onBackground,
                state = isStopped?.let { !it },
                enabled = trackLoaded.value
            ) {  viewModel.playbackControl() }
            PlayerButton(
                modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small_4x), top = dimensionResource(R.dimen.padding_small_15x))
                    .size(dimensionResource(R.dimen.play_btn_small))
                    .constrainAs(addToFavorites) {
                        end.linkTo(guidelineRight)
                        top.linkTo(play.top)
                        bottom.linkTo(play.bottom)
                        verticalBias = .5f
                    },
                icon = images.addToFavorites,
                activeIcon = images.addToFavoriteActives,
                state = isFavorite,
            ) { viewModel.addToFavorites() }
            SlidingTime(
                time = time,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_2x))
                    .constrainAs(currentTime) {
                        start.linkTo(play.start)
                        end.linkTo(play.end)
                        top.linkTo(play.bottom)
                        bottom.linkTo(playerDuration.top)
                    }
            )
            TextBox(
                modifier = Modifier.constrainAs(playerDuration) {
                        start.linkTo(guidelineLeft)
                        top.linkTo(currentTime.bottom)
                    },
                text = stringResource(R.string.player_duration),
                isCaption = true
            )
            TextBox(
                modifier = Modifier.constrainAs(playerDurationText) {
                        start.linkTo(barrierEnd)
                        end.linkTo(guidelineRight)
                        top.linkTo(playerDuration.top)
                        bottom.linkTo(playerDuration.bottom)
                        horizontalBias = 1f
                    },
                text = track?.duration?.millisToSeconds().toString()
            )

            TextBox(
                modifier = Modifier.constrainAs(albumTitle) {
                        start.linkTo(guidelineLeft)
                        top.linkTo(playerDuration.bottom)
                    },
                text = stringResource(R.string.player_album),
                isCaption = true
            )
            TextBox(
                modifier = Modifier.constrainAs(albumTitleText) {
                        start.linkTo(barrierEnd)
                        end.linkTo(guidelineRight)
                        top.linkTo(albumTitle.top)
                        bottom.linkTo(albumTitle.bottom)
                        horizontalBias = 1f
                    },
                text = track?.albumName.toString()
            )

            TextBox(
                modifier = Modifier.constrainAs(year) {
                        start.linkTo(guidelineLeft)
                        top.linkTo(albumTitle.bottom)
                    },
                text = stringResource(R.string.player_year),
                isCaption = true
            )
            TextBox(
                modifier = Modifier.constrainAs(yearText) {
                        start.linkTo(barrierEnd)
                        end.linkTo(guidelineRight)
                        top.linkTo(year.top)
                        bottom.linkTo(year.bottom)
                        horizontalBias = 1f
                    },
                text = track?.releaseDate.toString()
            )

            TextBox(
                modifier = Modifier.constrainAs(genre) {
                        start.linkTo(guidelineLeft)
                        top.linkTo(year.bottom)
                    },
                text = stringResource(R.string.player_genre),
                isCaption = true
            )
            TextBox(
                modifier = Modifier.constrainAs(genreText) {
                        start.linkTo(barrierEnd)
                        end.linkTo(guidelineRight)
                        top.linkTo(genre.top)
                        bottom.linkTo(genre.bottom)
                        horizontalBias = 1f
                    },
                text = track?.genre.toString()
            )

            TextBox(
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small_15x))
                    .constrainAs(country) {
                        start.linkTo(guidelineLeft)
                        top.linkTo(genre.bottom)
                    },
                text = stringResource(R.string.player_country),
                isCaption = true
            )
            TextBox(
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small_15x))
                    .constrainAs(countryText) {
                        start.linkTo(barrierEnd)
                        end.linkTo(guidelineRight)
                        top.linkTo(country.top)
                        bottom.linkTo(country.bottom)
                        horizontalBias = 1f
                    },
                text = trackDescription.country ?: stringResource(R.string.player_unknown)
            )
        }
    }
}

@Composable
fun CustomText(modifier: Modifier, text: String, size: Float) {
    AndroidView(
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.padding_small_4x))
            .fillMaxWidth(),
        factory = { context ->
            AutoScrollTextView(context).apply {
                gradientWidth = context.resources.getDimension(R.dimen.padding_small_8x).toInt()
                this.text = text
                setParams(size = size)
            }
        },
        update = { view ->
            view.text = text
        }
    )
}

@Composable
fun TextBox(modifier: Modifier, text: String, isCaption: Boolean = false) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            color = if (isCaption) colorResource(R.color.greyMedium) else MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier.padding(top = dimensionResource(R.dimen.padding_small_15x)),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun Progress(loaded: MutableState<Boolean>, modifier: Modifier, progress: Float) {
    if (loaded.value) return

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        if (progress == 100f) {
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 250)
            )
            loaded.value = true
        } else {
            animatedProgress.snapTo(0f)
        }
    }

    CircularProgressIndicator(
        progress = { animatedProgress.value },
        modifier = modifier.padding(top = dimensionResource(R.dimen.padding_small_15x))
            .size(dimensionResource(R.dimen.play_btn_big) - 6.dp),
        strokeWidth = 4.dp,
        color = MaterialTheme.colorScheme.onBackground
    )
}