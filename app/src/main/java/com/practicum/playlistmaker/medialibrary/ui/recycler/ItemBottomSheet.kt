package com.practicum.playlistmaker.medialibrary.ui.recycler

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.practicum.playlistmaker.R
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

@Composable
fun ItemBottomSheet(playlist: Playlist, onClick: () -> Unit) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(playlist.cover?.ifEmpty { null })
                .crossfade(true)
                .transformations(RoundedCornersTransformation(dimensionResource(R.dimen.list_cover_corner_radius).value))
                .build(),
            placeholder = painterResource(R.drawable.album_cover_stub),
            fallback = painterResource(R.drawable.album_cover_stub),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.padding_small_6x))
                .padding(vertical = dimensionResource(R.dimen.padding_small_4x))
                .size(dimensionResource(R.dimen.album_cover_size))
        )
        Column(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small_4x))
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = playlist.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_small)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = Util.formatValue(
                    playlist.tracksCount,
                    stringResource(R.string.playlist_track)
                ),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colorResource(R.color.greyMedium)
                )
            )
        }
    }
}