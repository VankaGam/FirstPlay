package com.example.playlistmaker.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.FavoriteTrackDao
import com.example.playlistmaker.data.mapper.FavoriteTrackMapper
import com.example.playlistmaker.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.domain.interactor.FavoritesInteractorImpl
import com.example.playlistmaker.search.data.local.SearchHistoryStorage
import com.example.playlistmaker.search.data.network.ApiService
import com.example.playlistmaker.search.data.network.RetrofitInstance
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    single<ApiService> { RetrofitInstance.api }

    single(named("search_history_prefs")) {
        androidContext()
            .getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    single(named("app_prefs")) {
        androidContext()
            .getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<SearchHistoryStorage> {
        SearchHistoryStorage(
            sharedPreferences = get(named("search_history_prefs")),
            gson = get()
        )
    }
    single {
        Room.databaseBuilder(
            get<Application>(),
            AppDatabase::class.java,
            "playlist_maker_db"
        ).build()
    }
    single { get<AppDatabase>().favoriteTrackDao() }
    single<FavoritesInteractor> { FavoritesInteractorImpl(get()) }
    single { FavoriteTrackMapper() }
}