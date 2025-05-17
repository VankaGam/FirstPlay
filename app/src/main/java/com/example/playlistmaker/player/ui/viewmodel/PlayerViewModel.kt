package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PlayerViewModel(
    private val interactor: PlayerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState())
    val state = _state.asStateFlow()

    init {
        interactor.isPlaying
            .onEach { playing -> updateState(isPlaying = playing) }
            .launchIn(viewModelScope)

        interactor.position
            .onEach { pos -> updateState(position = pos) }
            .launchIn(viewModelScope)
    }

    fun prepare(track: Track) = interactor.prepare(track)
    fun playPause() = interactor.playPause()

    fun release() = interactor.release()

    override fun onCleared()  = interactor.release()

    private fun updateState(
        isPlaying: Boolean = _state.value.isPlaying,
        position: Int = _state.value.position
    ) {
        _state.value = PlayerState(isPlaying, position)
    }
}