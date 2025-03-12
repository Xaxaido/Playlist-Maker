package com.practicum.playlistmaker.search.ui.recycler

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.practicum.playlistmaker.R

@Composable
fun ItemHeader() {
    Text(
        text = stringResource(R.string.search_history_title),
        style = MaterialTheme.typography.headlineMedium.copy(
            color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_12x))
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.search_history_header_height)),
        textAlign = TextAlign.Center
    )
}