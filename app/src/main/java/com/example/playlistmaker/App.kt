package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.DomainModule
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.mediaModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.di.navigationModule
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                interactorModule,
                viewModelModule,
                mediaModule,
                navigationModule,
                DomainModule
            )
        }

        val settingsRepo = GlobalContext.get().get<SettingsRepository>()

        val isDark = settingsRepo.isDarkTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (isDark)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}