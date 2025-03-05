package com.practicum.playlistmaker.medialibrary.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.CreatePlaylistState
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.ui.view_model.CreatePlaylistViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CreatePlaylist(
    navController: NavController,
    gson: Gson,
    playlistJson: String? = null,
    onBackClick: MutableState<(() -> Unit)?>
) {
    val context = LocalContext.current
    val viewModel: CreatePlaylistViewModel = koinViewModel { parametersOf(playlistJson) }
    var showDialog by remember { mutableStateOf(false) }
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    val playlist by remember { mutableStateOf(gson.fromJson(playlistJson, Playlist::class.java)) }
    var coverUri by remember { mutableStateOf<Uri?>(null) }
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            if (getPermissions(context, uri)) {
                coverUri = uri
                playlist?.cover = null
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sharedFlow.collect { state ->
            when (state) {
                is CreatePlaylistState.Create -> {
                    if (title.isNotEmpty()) {

                        if (playlist == null) {
                            Toast.makeText(
                                context,
                                String.format(
                                    context.getString(R.string.playlist_created),
                                    title
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        navigateBack(navController = navController, onBackClick = onBackClick)
                    }
                }

                is CreatePlaylistState.Edit -> {
                    playlist?.let {
                        coverUri = Uri.parse(it.cover)
                        title = it.name
                        description = it.description.toString()
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.isEditMode()
        onBackClick.value = {
            if (title.isEmpty() && description.isEmpty()) {
                navigateBack(navController = navController, onBackClick = onBackClick)
            } else {
                showDialog = true
            }
        }
    }

    BackHandler(true) {
        onBackClick.value?.invoke()
    }

    if (showDialog) {
        ConfirmDialog(
            visible = showDialog,
            title = stringResource(R.string.create_playlist_dialog_title),
            text = stringResource(R.string.create_playlist_dialog_message),
            dismissButtonText = stringResource(R.string.create_playlist_dialog_cancel),
            confirmButtonText = stringResource(R.string.create_playlist_dialog_finish),
            onDismiss = {
                showDialog = false
            },
            onConfirm = {
                showDialog = false
                navigateBack(navController = navController, onBackClick = onBackClick)
            },
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(1f)
                .padding(horizontal = dimensionResource(id = R.dimen.padding_small_12x))
                .padding(top = dimensionResource(id = R.dimen.padding_small_13x))
                .padding(bottom = dimensionResource(id = R.dimen.padding_small_8x)),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Image(
                painter = coverUri?.let {
                    rememberAsyncImagePainter(model = coverUri)
                } ?: painterResource(id = R.drawable.ic_add_photo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
                    .clickable {
                        pickMedia.launch(arrayOf("image/*"))
                    }
                    .drawWithContent {
                        drawContent()

                        if (coverUri == null) {
                            val strokeWidth = 2.dp.toPx()
                            val dashWidth = 36.dp.toPx()
                            val gapWidth = 36.dp.toPx()

                            drawRoundRect(
                                color = Color.Gray,
                                style = Stroke(
                                    width = strokeWidth,
                                    pathEffect = PathEffect.dashPathEffect(
                                        intervals = floatArrayOf(dashWidth, gapWidth),
                                        phase = 0f
                                    )
                                ),
                                cornerRadius = CornerRadius(12.dp.toPx()),
                                size = size
                            )
                        }

                    }
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            PlaylistTitleInput(
                text = title,
                label = stringResource(R.string.playlist_name),
            ) { newValue ->
                title = newValue
            }
            PlaylistTitleInput(
                text = description,
                label = stringResource(R.string.playlist_description),
            ) { newValue ->
                description = newValue
            }
        }
        BlueButton(
            enabled = title.isNotEmpty(),
            isInEditMode = playlist != null
        ) {
            viewModel.createPlaylist(
                playlist?.id,
                playlist?.cover ?: coverUri?.let { viewModel.saveImage(it.toString()) } ?: "",
                title,
                description,
                playlist?.tracksCount,
            )
        }
    }
}

@Composable
fun PlaylistTitleInput(text: String, label: String, onValueChange: (String) -> Unit) {
    val textSelectionColors = TextSelectionColors(
        handleColor = colorResource(R.color.blue),
        backgroundColor = colorResource(R.color.blue).copy(alpha = 0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            label = { Text(label) },
            maxLines = 1,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_8x))
                .padding(horizontal = dimensionResource(R.dimen.padding_small_8x))
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = colorResource(R.color.blue),
                cursorColor = colorResource(R.color.blue),
            )
        )
    }
}

@Composable
fun BlueButton(
    enabled : Boolean,
    isInEditMode: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small_8x))
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.blue),
            disabledContainerColor = colorResource(R.color.greyMedium),
        ),
    ) {
        Text(
            text = if (isInEditMode) stringResource(R.string.edit) else stringResource(R.string.btn_create),
            style = MaterialTheme.typography.titleMedium.copy(
                color = colorResource(R.color.white)
            )
        )
    }
}

fun navigateBack(navController: NavController, onBackClick: MutableState<(() -> Unit)?>) {
    onBackClick.value = null
    navController.navigateUp()
}

private fun getPermissions(context: Context, uri: Uri): Boolean {
    val contentResolver = context.contentResolver
    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

    return try {
        contentResolver.takePersistableUriPermission(uri, takeFlags)
        true
    } catch(e: SecurityException) {
        false
    }
}