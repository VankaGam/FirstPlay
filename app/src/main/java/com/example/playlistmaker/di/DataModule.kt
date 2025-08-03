package com.example.playlistmaker.di

import android.app.Application
import android.content.Context
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
}