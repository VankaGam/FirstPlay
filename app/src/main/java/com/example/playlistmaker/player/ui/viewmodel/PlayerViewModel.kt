package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.player.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.service.AudioPlayerBar
import com.example.playlistmaker.player.service.AudioPlayerService
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val interactor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
    initialTrack: Track
) : ViewModel() {

    private var audioPlayerService: AudioPlayerService? = null
    private var playerBar: AudioPlayerBar? = null
    private var svcJobs: Job? = null
    private val _state = MutableStateFlow(PlayerState(track = initialTrack))
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    val playlists: StateFlow<List<Playlist>> =
        playlistInteractor.observeAll()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    sealed class AddResult {
        data class Added(val playlistName: String): AddResult()
        data class AlreadyThere(val playlistName: String): AddResult()
    }
    private val _addResult = MutableSharedFlow<AddResult>()
    val addResult = _addResult.asSharedFlow()

    init {
        interactor.isPlaying
            .onEach { playing ->
                _state.update { it.copy(isPlaying = playing) }
            }
            .launchIn(viewModelScope)

        interactor.position
            .onEach { pos ->
                _state.update { it.copy(position = pos) }
            }
            .launchIn(viewModelScope)

        favoritesInteractor.observeFavorites()
            .map { favs -> favs.any { it.trackId == initialTrack.trackId } }
            .distinctUntilChanged()
            .onEach { isFav ->
                _state.update { it.copy(isFavorite = isFav) }
            }
            .launchIn(viewModelScope)
    }

    fun onAddCurrentTrackTo(playlist: Playlist) {
        val track = _state.value.track
        if (playlist.trackIds.contains(track.trackId.toLong())) {
            viewModelScope.launch { _addResult.emit(AddResult.AlreadyThere(playlist.name)) }
        } else {
            viewModelScope.launch {
                val added = playlistInteractor.addTrackToPlaylist(playlist.id, track)
                if (added) _addResult.emit(AddResult.Added(playlist.name))
                else _addResult.emit(AddResult.AlreadyThere(playlist.name))
            }
        }
    }

    fun prepare(track: Track) = interactor.prepare(track)
    fun playPause() = interactor.playPause()
    fun release() = interactor.release()

    override fun onCleared() = interactor.release()

    fun onFavoriteClicked() {
        val current = _state.value.track
        viewModelScope.launch {
            if (_state.value.isFavorite) {
                favoritesInteractor.removeFromFavorites(current)
            } else {
                favoritesInteractor.addToFavorites(current)
            }
            _state.update { it.copy(isFavorite = !it.isFavorite) }
        }
    }

    fun attachService(service: AudioPlayerService) {
        playerBar = service
        audioPlayerService = service
    }

    fun detachService() {
        playerBar = null
        svcJobs?.cancel()
        svcJobs = null
        audioPlayerService = null
    }

    fun onPlayPauseClicked() {
        playerBar?.let { g ->
            if (g.isPlaying()) g.pause() else g.play()
            return
        }
        playPause()
    }

    fun onUiVisible() {
        playerBar?.hideNotification()
    }

    fun onUiHidden() {
        playerBar?.let { if (it.isPlaying()) it.showNotification() }
    }

    fun onScreenClosed() {
        playerBar?.stop()
        audioPlayerService?.stop()
    }

}