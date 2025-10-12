package com.example.playlistmaker.search.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.viewmodel.SearchState
import kotlinx.coroutines.delay
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.icons.filled.ChevronRight

val YSDisplay = FontFamily(
    Font(R.font.ys_display_regular, FontWeight.Normal), // 400
    Font(R.font.ys_display_medium,  FontWeight.Medium)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    localNoNetworkError: Boolean,
    forceEmptyResults: Boolean,
    onTextChangedSideEffects: () -> Unit,
    onDebouncedQuery: (String) -> Unit,
    onClearButton: () -> Unit,
    onRefresh: (String) -> Unit,
    onClearHistory: () -> Unit,
    onTrackClick: (Track) -> Unit,
    onRestore: () -> Unit
) {
    var text by remember { mutableStateOf(state.query) }
    var isTypingDebounce by remember { mutableStateOf(false) }

    LaunchedEffect(state.query) {
        if (state.query != text) text = state.query
    }
    LaunchedEffect(Unit) { onRestore() }
    LaunchedEffect(text) {
        onTextChangedSideEffects()
        isTypingDebounce = true
        if (text.isEmpty()) {
            onDebouncedQuery("")
            isTypingDebounce = false
        } else {
            delay(2000)
            onDebouncedQuery(text)
            isTypingDebounce = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.search),
                        style = LocalTextStyle.current.copy(
                            fontFamily = YSDisplay,
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp,
                            lineHeight = 22.sp,
                            letterSpacing = 0.sp
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.color_background),
                    scrolledContainerColor = colorResource(R.color.color_background),
                    titleContentColor = colorResource(R.color.color_special_black),
                    navigationIconContentColor = colorResource(R.color.color_special_black),
                    actionIconContentColor = colorResource(R.color.color_special_black)
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colorResource(R.color.color_background))
        ) {
            SearchRowXmlLike(
                value = text,
                onValueChange = { new -> text = new },
                onClear = {
                    text = ""
                    onClearButton()
                },
                onDone = { onDebouncedQuery(text) },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            val hasQuery = text.isNotEmpty()
            val noResults = state.tracks.isEmpty()

            when {
                isTypingDebounce && hasQuery -> Unit
                forceEmptyResults -> Unit
                !hasQuery && state.showHistory && state.history.isNotEmpty() -> {
                    HistoryBlock(
                        items = state.history,
                        onClear = onClearHistory,
                        onItemClick = onTrackClick
                    )
                }

                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                localNoNetworkError && hasQuery -> {
                    ErrorPlaceholder(onRefresh = { onRefresh(text) })
                }

                hasQuery && !state.isLoading && !localNoNetworkError && noResults -> {
                    EmptyPlaceholder()
                }

                state.tracks.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.tracks, key = { it.trackId }) { t ->
                            TrackRowXmlLike(t, onTrackClick)
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun HistoryBlock(
    items: List<Track>,
    onClear: () -> Unit,
    onItemClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val isLongList by remember {
        derivedStateOf { listState.canScrollForward || listState.firstVisibleItemIndex > 0 }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 13.dp, end = 12.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = stringResource(R.string.were_looking),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = YSDisplay,
                    fontWeight = FontWeight.Medium,
                    fontSize = 19.sp,
                    lineHeight = 19.sp,
                    color = colorResource(R.color.color_special_black)
                )
            )

            if (isLongList) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 56.dp), // запас под кнопку
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items, key = { it.trackId }) { t ->
                        TrackRowXmlLike(t, onItemClick)
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.wrapContentHeight(),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items, key = { it.trackId }) { t ->
                        TrackRowXmlLike(t, onItemClick)
                    }
                }

                Spacer(Modifier.height(16.dp))

                ClearHistoryButton(
                    onClick = onClear,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        if (isLongList) {
            ClearHistoryButton(
                onClick = onClear,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun ClearHistoryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(36.dp)
            .defaultMinSize(minWidth = 0.dp)
            .wrapContentWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.color_black),
            contentColor = colorResource(R.color.white)
        )
    ) {
        Text(
            text = stringResource(R.string.clear_history),
            style = TextStyle(
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 14.sp
            )
        )
    }
}

@Composable
private fun SearchRowXmlLike(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ysTextStyle = TextStyle(
        fontFamily = YSDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )

    Row(
        modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.white_navigator)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = colorResource(R.color.iconSearch),
            modifier = Modifier
                .padding(start = 12.dp)
                .size(18.dp)
        )

        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = stringResource(R.string.search),
                    style = ysTextStyle,
                    color = colorResource(R.color.search_hint_color)
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = ysTextStyle.copy(color = colorResource(R.color.black_text_search)),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                cursorBrush = SolidColor(colorResource(R.color.black_text_search)),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (value.isNotEmpty()) {
            IconButton(
                onClick = onClear,
                modifier = Modifier
                    .padding(start = 6.dp, end = 12.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = null,
                    tint = colorResource(R.color.iconSearch),
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Spacer(Modifier.width(24.dp))
        }
    }
}

@Composable
fun ErrorPlaceholder(onRefresh: () -> Unit) {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Image(
            painter = painterResource(R.drawable.error_internet),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.error_internet_text),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 19.sp,
                lineHeight = 19.sp,
                letterSpacing = 0.sp,
                color = colorResource(R.color.color_special_black)
            )
        )

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRefresh,
            modifier = Modifier
                .height(36.dp)
                .defaultMinSize(minWidth = 0.dp)
                .wrapContentWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.color_black),
                contentColor = colorResource(R.color.white)
            )
        ) {
            Text(
                text = stringResource(R.string.refresh_button_text),
                style = TextStyle(
                    fontFamily = YSDisplay,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 0.sp
                )
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun EmptyPlaceholder() {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Image(
            painter = painterResource(R.drawable.error_search),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.error_search_text),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 19.sp,
                lineHeight = 19.sp,
                letterSpacing = 0.sp,
                color = colorResource(R.color.color_special_black)
            )
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TrackRowXmlLike(
    track: Track,
    onClick: (Track) -> Unit
) {
    val SecondaryGrey = colorResource(R.color.color_gray)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(track) })
            .padding(horizontal = 13.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(3.dp))
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = track.trackName,
                maxLines = 1,
                style = TextStyle(
                    fontFamily = YSDisplay,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.sp,
                    color = colorResource(R.color.color_special_black)
                )
            )

            Spacer(Modifier.height(3.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    maxLines = 1,
                    style = TextStyle(
                        fontFamily = YSDisplay,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 0.sp,
                        color = SecondaryGrey
                    )
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(3.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(SecondaryGrey)
                )
                Text(
                    text = track.getFormattedTrackTime(),
                    maxLines = 1,
                    style = TextStyle(
                        fontFamily = YSDisplay,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 0.sp,
                        color = SecondaryGrey
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = colorResource(R.color.color_gray),
            modifier = Modifier.size(20.dp)
        )
    }
}