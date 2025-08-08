package com.example.playlistmaker.di

import com.example.playlistmaker.media.domain.interactor.FavoritesInteractorImpl
import com.example.playlistmaker.player.domain.interactor.FavoritesInteractor
import org.koin.dsl.module

val DomainModule = module {
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(
            repository = get() 
        )
    }
}