package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PlayerViewModelFactory(
    private val interactor: PlayerRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(interactor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}