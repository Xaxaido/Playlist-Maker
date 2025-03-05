package com.practicum.playlistmaker.medialibrary.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MediaLibraryScreen(navController: NavController, gson: Gson) {

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val selectedTabIndex = pagerState.currentPage

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .height(2.dp)
                        .padding(horizontal = dimensionResource(R.dimen.padding_small_8x))
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground)
                )
            },
            divider = {},
        ) {
            PagerTab(scope = scope, pagerState = pagerState, index = 0, selectedTabIndex = selectedTabIndex, text = stringResource(R.string.favorite_tracks))
            PagerTab(scope = scope, pagerState = pagerState, index = 1, selectedTabIndex = selectedTabIndex, text = stringResource(R.string.playlists))
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when(page) {
                0 -> FavoriteTracksScreen(navController = navController)
                1 -> PlaylistsScreen(navController = navController, gson = gson)
            }
        }
    }
}

@Composable
fun PagerTab(scope: CoroutineScope, pagerState: PagerState, index: Int, selectedTabIndex: Int, text: String) {
    Tab(
        selected = selectedTabIndex == index,
        selectedContentColor = MaterialTheme.colorScheme.onBackground,
        unselectedContentColor = MaterialTheme.colorScheme.onBackground,
        onClick = {
            scope.launch {
                pagerState.animateScrollToPage(index)
            }
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color =  MaterialTheme.colorScheme.onBackground
                ),
            )
        }
    )
}