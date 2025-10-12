package com.example.playlistmaker.di

import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.ui.MediaLibraryViewModel
import com.example.playlistmaker.media.ui.PlaylistUi
import com.example.playlistmaker.player.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.first
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mediaLibraryComposeModule = module {

    single<suspend () -> List<Track>>(named("getFavoritesOnce")) {
        suspend { get<FavoritesInteractor>().observeFavorites().first() }
    }

    single<suspend () -> List<PlaylistUi>>(named("getPlaylistsOnce")) {
        suspend {
            get<PlaylistInteractor>().observeAll().first().map { p ->
                PlaylistUi(
                    id = p.id,
                    title = p.name,
                    trackCount = p.trackCount,
                    coverUrl = p.coverPath
                )
            }
        }
    }

    viewModel {
        MediaLibraryViewModel(
            getFavoritesOnce = get(named("getFavoritesOnce")),
            getPlaylistsOnce = get(named("getPlaylistsOnce")),
        )
    }
}