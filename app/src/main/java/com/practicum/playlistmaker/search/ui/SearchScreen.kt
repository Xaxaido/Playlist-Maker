package com.practicum.playlistmaker.search.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.SearchState
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.main.ui.Routes
import com.practicum.playlistmaker.medialibrary.ui.NothingToShow
import com.practicum.playlistmaker.medialibrary.ui.Progress
import com.practicum.playlistmaker.search.ui.recycler.ItemFooter
import com.practicum.playlistmaker.search.ui.recycler.ItemHeader
import com.practicum.playlistmaker.search.ui.recycler.ItemTrack
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: SearchViewModel = koinViewModel()
    val focusManager: FocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val state by viewModel.stateFlow.collectAsState()
    val scope = rememberCoroutineScope()
    val isClickEnabled = remember { mutableStateOf(true) }
    val searchRequest = rememberSaveable { mutableStateOf("")}
    val hasFocus = remember { mutableStateOf(false) }
    val btnClearVisibility = searchRequest.value.isNotEmpty()
    val listState = rememberLazyListState()
    val hazeState = remember { HazeState() }
    var isLastItemVisible by remember { mutableStateOf(true) }
    viewModel.isHistoryVisible = state is SearchState.TrackSearchHistory

    if (state is SearchState.NoData) {
        focusManager.clearFocus()
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            totalItemsCount == 0 || visibleItems.any { it.index == totalItemsCount - 1 }
        }.collect { isVisible ->
            isLastItemVisible = isVisible
        }
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (searchBox, guidelineTop, guidelineBottom, stickyContainer, progress, nothingFound, networkFailure) = createRefs()

        TracksList(
            isLastItemVisible = isLastItemVisible,
            isHistoryVisible = viewModel.isHistoryVisible ?: false,
            hazeState = hazeState,
            listState = listState,
            visible = state is SearchState.TrackSearchResults || state is SearchState.TrackSearchHistory,
            state = state,
            viewModel = viewModel,
            scope = scope,
            navController = navController,
            isClickEnabled = isClickEnabled
        )
        Search(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.toolbar_height))
                .constrainAs(searchBox) {
                    top.linkTo(parent.top)
                },
            searchRequest = searchRequest,
            btnClearVisibility = btnClearVisibility,
            hasFocus = hasFocus,
            viewModel = viewModel
        ) {
            keyboardController?.hide()
            searchRequest.value = ""
            viewModel.clearSearchRequest()
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.padding_small_8x))
                .constrainAs(guidelineTop) {
                    top.linkTo(searchBox.bottom)
                }
        )
        ItemFooter(
            modifier = Modifier.constrainAs(stickyContainer) {
                    bottom.linkTo(guidelineBottom.top)
                },
            visible = !isLastItemVisible && viewModel.isHistoryVisible ?: false,
            onClearHistoryBtnClick  = {
                viewModel.clearHistory()
            }
        )
        Progress(
            modifier = Modifier.padding(top = dimensionResource(R.dimen.ui_margin_top))
                .fillMaxWidth()
                .constrainAs(progress) {
                    top.linkTo(guidelineTop.bottom)
                },
            visible = state is SearchState.Loading
        )
        NothingToShow(
            modifier = Modifier.fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.ui_margin_top))
                .constrainAs(nothingFound) {
                    top.linkTo(guidelineTop.bottom)
                },
            visible = state is SearchState.NothingFound, stringResource(R.string.nothing_found)
        )
        NetworkFailure(
            modifier = Modifier.padding(top = dimensionResource(R.dimen.ui_margin_top))
                .fillMaxSize()
                .constrainAs(networkFailure) {
                    top.linkTo(guidelineTop.bottom)
                },
            visible = state is SearchState.ConnectionError,
            stringResource(R.string.network_failure),
            stringResource(R.string.update)
        ) {
            viewModel.search(searchRequest.value)
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.toolbar_height))
                .constrainAs(guidelineBottom) {
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}

@Composable
fun TracksList(
    isLastItemVisible: Boolean,
    isHistoryVisible: Boolean,
    hazeState: HazeState,
    listState: LazyListState,
    visible: Boolean,
    state: SearchState?,
    viewModel: SearchViewModel,
    scope: CoroutineScope,
    navController: NavController,
    isClickEnabled: MutableState<Boolean>
) {
    if (!visible) return

    val tracks = when (state) {
        is SearchState.TrackSearchResults -> {
            state.results
        }
        is SearchState.TrackSearchHistory -> {
            state.history
        }
        else -> null
    }

    if (tracks == null) return

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .hazeSource(state = hazeState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = listState,
        contentPadding = PaddingValues(top = dimensionResource(R.dimen.toolbar_height), bottom = dimensionResource(R.dimen.toolbar_height))
    ) {
        item {
            if (isHistoryVisible) {
                ItemHeader()
            }
        }
        items(tracks) { item ->
            ItemTrack(
                track = item,
                onClick =  {
                    if (isClickEnabled.value) {
                        isClickEnabled.value = false
                        viewModel.addToHistory(item)
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
        item {
            if (isHistoryVisible) {
                ItemFooter(modifier = Modifier, visible = isLastItemVisible, onClearHistoryBtnClick = { viewModel.clearHistory() })
            }
        }
    }
}

@Composable
fun NetworkFailure(modifier: Modifier, visible: Boolean, message: String, btnTitle: String, onClick: () -> Unit) {
    if (!visible) return

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            alignment = Alignment.Center,
            painter = painterResource(id = R.drawable.network_failure),
            contentDescription = null,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.headlineLarge.copy(
                color = colorResource(R.color.textButton_TextColor),
            ),
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small_6x)),
            textAlign = TextAlign.Center,
        )
        DarkButton(
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small_12x)),
            text = btnTitle
        ) { onClick() }
    }
}

@Composable
fun Search(
    modifier: Modifier,
    searchRequest: MutableState<String>,
    hasFocus: MutableState<Boolean>,
    btnClearVisibility: Boolean,
    viewModel: SearchViewModel,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small_8x), vertical = dimensionResource(R.dimen.padding_small_4x))
                .fillMaxWidth()
                .height(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = colorResource(R.color.editText_BgColor)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_small_6x))
                    .padding(vertical = dimensionResource(R.dimen.padding_small_5x))
                    .width(18.dp)
                    .height(16.dp),
                painter = painterResource(id = R.drawable.ic_menu_search),
                colorFilter = ColorFilter.tint(colorResource(R.color.search_hint_color)),
                contentDescription = null,
            )
            SearchTextField(
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small_4x))
                    .weight(1f),
                searchRequest = searchRequest,
                hasFocus = hasFocus,
                viewModel = viewModel
            )
            ClearBtn(visible = btnClearVisibility, onClick = onClick)
        }
    }
}

@Composable
fun SearchTextField(
    modifier: Modifier,
    searchRequest: MutableState<String>,
    hasFocus: MutableState<Boolean>,
    viewModel: SearchViewModel,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        if (searchRequest.value.isEmpty()) {
            Text(
                text = stringResource(R.string.search_hint),
                color = colorResource(R.color.search_hint_color),
                fontSize = dimensionResource(id = R.dimen.text_medium).value.sp
            )
        }

        val textSelectionColors = TextSelectionColors(
            handleColor = colorResource(R.color.blue),
            backgroundColor = colorResource(R.color.blue).copy(alpha = 0.4f)
        )
        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
                    .onFocusChanged { focusState ->
                        hasFocus.value = focusState.isFocused
                        if (hasFocus.value && searchRequest.value.isEmpty() && viewModel.isHistoryVisible != true) {
                            viewModel.getHistory(true)
                        }
                    },
                value = searchRequest.value,
                onValueChange = { newValue: String ->
                    searchRequest.value = newValue

                    if (hasFocus.value) viewModel.search(searchRequest.value)
                    if (hasFocus.value && searchRequest.value.isEmpty()) {
                        viewModel.getHistory(true)
                    }
                },
                textStyle = TextStyle(
                    color = colorResource(R.color.black),
                    fontSize = dimensionResource(id = R.dimen.text_medium).value.sp,
                    textAlign = TextAlign.Start
                ),
                cursorBrush = SolidColor(colorResource(R.color.blue)),
            )
        }
    }
}

@Composable
fun ClearBtn(visible: Boolean, onClick: () -> Unit) {
    if (!visible) return

    Image(
        modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small_4x))
            .clickable {
                onClick()
            },
        painter = painterResource(id = R.drawable.ic_close),
        colorFilter = ColorFilter.tint(colorResource(R.color.search_hint_color)),
        contentDescription = null,
    )
}