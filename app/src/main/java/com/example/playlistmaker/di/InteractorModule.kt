package com.example.playlistmaker.di

import com.example.playlistmaker.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.search.domain.usecase.SaveTrackToHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.settings.domain.usecase.GetThemeModeUseCase
import com.example.playlistmaker.settings.domain.usecase.SetThemeModeUseCase
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.data.interactor.PlayerInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory { SearchTracksUseCase(get()) }
    factory { SaveTrackToHistoryUseCase(get()) }
    factory { GetSearchHistoryUseCase(get()) }
    factory { ClearSearchHistoryUseCase(get()) }
    factory { GetThemeModeUseCase(get()) }
    factory { SetThemeModeUseCase(get()) }
    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }
}