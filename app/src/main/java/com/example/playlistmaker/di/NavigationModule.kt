package com.example.playlistmaker.di

import com.example.playlistmaker.core.navigation.NavigatorHolder
import com.example.playlistmaker.core.navigation.Router
import com.example.playlistmaker.core.navigation.RouterImpl
import org.koin.dsl.module

val navigationModule = module {
    single<Router> { RouterImpl() }
    single<NavigatorHolder> { get<Router>().navigatorHolder }
}