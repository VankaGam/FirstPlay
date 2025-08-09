package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _saving = MutableStateFlow(false)
    val saving = _saving.asStateFlow()

    fun create(name: String, description: String?, coverPath: String?, onDone: (Long) -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            _saving.value = true
            try {
                val id = interactor.create(name, description, coverPath)
                onDone(id)
            } catch (t: Throwable) {
                onError(t)
            } finally {
                _saving.value = false
            }
        }
    }
}