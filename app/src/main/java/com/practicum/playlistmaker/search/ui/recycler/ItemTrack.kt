package com.practicum.playlistmaker.search.ui.recycler

import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.common.widgets.textview.EllipsizeTextView
import com.practicum.playlistmaker.search.domain.model.Track

@Composable
fun ItemTrack(track: Track, onClick: () -> Unit) {
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() }
    ) {
        val (cover, favorite, trackData, btnTrack) = createRefs()

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(track.albumCover?.ifEmpty { null })
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
                .constrainAs(cover) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        Favorite(
            visible = track.isFavorite,
            modifier = Modifier.size(16.dp)
                .constrainAs(favorite) {
                    end.linkTo(cover.end, margin = (-8).dp)
                    bottom.linkTo(parent.bottom)
                }
        )

        Column(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small_4x))
                .constrainAs(trackData) {
                    start.linkTo(cover.end)
                    end.linkTo(btnTrack.start)
                    bottom.linkTo(cover.bottom)
                    top.linkTo(cover.top)
                    width = Dimension.fillToConstraints
                    verticalBias = 0.5f
                }
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            AndroidView(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_small)),
                factory = { context ->
                    EllipsizeTextView(ContextThemeWrapper(context, R.style.DescriptionText)).apply {
                        setText(track.artistName, track.duration.millisToSeconds())
                    }
                }
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier
                .padding(end = dimensionResource(R.dimen.padding_small_6x))
                .size(24.dp)
                .constrainAs(btnTrack) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
        )
    }
}

@Composable
fun Favorite(visible: Boolean, modifier: Modifier) {
    if (!visible) return

    Image(
        painter = painterResource(id = R.drawable.ic_favorite),
        contentDescription = null,
        modifier = modifier
    )
}