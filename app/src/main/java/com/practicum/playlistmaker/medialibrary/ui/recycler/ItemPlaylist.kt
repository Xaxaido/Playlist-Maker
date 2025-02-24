package com.practicum.playlistmaker.medialibrary.ui.recycler

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

@Composable
fun ItemPlaylist(playlist: Playlist, onClick: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(playlist.cover?.ifEmpty { null })
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.album_cover_stub),
                fallback = painterResource(R.drawable.album_cover_stub),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = playlist.name,
            style = TextStyle(
                fontSize = dimensionResource(id = R.dimen.text_small).value.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.padding_small_2x))
        )
        Text(
            text = Util.formatValue(playlist.tracksCount, stringResource(R.string.playlist_track)),
            style = TextStyle(
                fontSize = dimensionResource(id = R.dimen.text_small).value.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.padding_small))
        )
    }
}