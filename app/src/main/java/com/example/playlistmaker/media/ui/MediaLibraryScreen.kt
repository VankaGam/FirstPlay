package com.example.playlistmaker.media.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.search.ui.YSDisplay

@Composable
fun MediaLibraryScreen(
    state: MediaLibraryState,
    onTabSelect: (Int) -> Unit,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (PlaylistUi) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    @DrawableRes emptyFavoritesImage: Int,
    @DrawableRes emptyPlaylistsImage: Int,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.color_background))
            .padding(bottom = 0.dp)
    ) {
        Text(
            text = stringResource(id = R.string.media_library_title),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            style = LocalTextStyle.current.copy(
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.sp,
                color = colorResource(R.color.color_special_black)
            )
        )

        TabRow(
            selectedTabIndex = state.selectedTab,
            containerColor = colorResource(R.color.color_background),
            contentColor = colorResource(R.color.color_special_black),
            indicator = { positions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(positions[state.selectedTab]),
                    height = 2.dp,
                    color = colorResource(R.color.color_special_black)
                )
            },
            divider = {}
        ) {
            Tab(
                selected = state.selectedTab == 0,
                onClick = { onTabSelect(0) },
                text = {
                    Text(
                        text = stringResource(id = R.string.tab_favorites),
                        style = LocalTextStyle.current.copy(
                            fontFamily = YSDisplay,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            color = colorResource(R.color.color_special_black)
                        )
                    )
                }
            )
            Tab(
                selected = state.selectedTab == 1,
                onClick = { onTabSelect(1) },
                text = {
                    Text(
                        text = stringResource(id = R.string.tab_playlists),
                        style = LocalTextStyle.current.copy(
                            fontFamily = YSDisplay,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            color = colorResource(R.color.color_special_black)
                        )
                    )
                }
            )
        }

        when (state.selectedTab) {
            0 -> FavoritesTab(
                items = state.favorites,
                onItemClick = onTrackClick,
                emptyImage = emptyFavoritesImage,
                emptyText = stringResource(R.string.media_empty_library)
            )
            else -> PlaylistsTab(
                items = state.playlists,
                onItemClick = onPlaylistClick,
                onCreatePlaylistClick = onCreatePlaylistClick,
                emptyImage = emptyPlaylistsImage,
                emptyText = stringResource(R.string.media_empty_playlists)
            )
        }
    }
}

@Composable
private fun FavoritesTab(
    items: List<Track>,
    onItemClick: (Track) -> Unit,
    @DrawableRes emptyImage: Int,
    emptyText: String
) {
    if (items.isEmpty()) {
        EmptyState(image = emptyImage, message = emptyText)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 24.dp, bottom = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items, key = { it.trackId }) { t ->
                TrackRowLibrary(t, onItemClick)
            }
        }
    }
}

@Composable
private fun TrackRowLibrary(track: Track, onClick: (Track) -> Unit) {
    val primary = colorResource(R.color.color_special_black)
    val secondary = colorResource(R.color.color_gray)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(track) }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(6.dp))
        )
        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = track.trackName,
                maxLines = 1,
                style = LocalTextStyle.current.copy(
                    fontFamily = YSDisplay,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    color = primary
                )
            )
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    maxLines = 1,
                    style = LocalTextStyle.current.copy(
                        fontFamily = YSDisplay,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        color = secondary
                    )
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(3.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(secondary)
                )
                Text(
                    text = track.getFormattedTrackTime(),
                    maxLines = 1,
                    style = LocalTextStyle.current.copy(
                        fontFamily = YSDisplay,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        color = secondary
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = secondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PlaylistsTab(
    items: List<PlaylistUi>,
    onItemClick: (PlaylistUi) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    @DrawableRes emptyImage: Int,
    emptyText: String
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onCreatePlaylistClick,
            modifier = Modifier
                .height(36.dp)
                .defaultMinSize(minWidth = 0.dp)
                .wrapContentWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.color_black),
                contentColor = colorResource(R.color.white)
            ),
            contentPadding = PaddingValues(
                horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.create_playlist),
                style = LocalTextStyle.current.copy(
                    fontFamily = YSDisplay,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 14.sp
                )
            )
        }

        if (items.isEmpty()) {
            Spacer(Modifier.height(24.dp))
            EmptyState(image = emptyImage, message = emptyText)
        } else {
            // сетка 2 колонки, отступы Start/End 8dp, Top 16dp
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 8.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items, key = { it.id }) { p ->
                    PlaylistCard(item = p, onClick = { onItemClick(p) })
                }
            }
        }
    }
}

@Composable
private fun PlaylistCard(item: PlaylistUi, onClick: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val data: Any? = item.coverUrl?.let { path ->
        when {
            path.isBlank() -> null
            path.startsWith("http", true) -> path
            path.startsWith("content://", true) -> android.net.Uri.parse(path)
            else -> java.io.File(path)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = coil.request.ImageRequest.Builder(context)
                .data(data)
                .crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = painterResource(R.drawable.zaglyshka),
            error = painterResource(R.drawable.zaglyshka),
            fallback = painterResource(R.drawable.zaglyshka),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(Modifier.height(6.dp))
        Text(
            text = item.title,
            maxLines = 1,
            style = LocalTextStyle.current.copy(
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                color = colorResource(R.color.black)
            )
        )
        Text(
            text = stringResource(R.string.playlist_tracks_count, item.trackCount),
            style = LocalTextStyle.current.copy(
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = colorResource(R.color.black)
            )
        )
    }
}

@Composable
private fun EmptyState(@DrawableRes image: Int, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = image), contentDescription = null, modifier = Modifier.size(120.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 19.sp,
                lineHeight = 19.sp,
                color = colorResource(R.color.black)
            )
        )
    }
}