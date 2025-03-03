package com.practicum.playlistmaker.search.ui.recycler

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.ui.DarkButton

@Composable
fun ItemFooter(
    modifier: Modifier,
    visible: Boolean,
    onClearHistoryBtnClick: () -> Unit
) {
    if (!visible) return

    Box(
        modifier = modifier.fillMaxWidth()
            .height(dimensionResource(R.dimen.clear_search_history_btn_container_height)),
        contentAlignment = Alignment.TopCenter
    ) {
        DarkButton(
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_12x)),
            text = stringResource(R.string.search_history_btn_clear),
            onClick = onClearHistoryBtnClick
        )
    }
}