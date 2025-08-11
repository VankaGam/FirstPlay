package com.example.playlistmaker.playlist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistWorkViewModel(
    private val interactor: PlaylistInteractor,
    private val playlistId: Long
) : ViewModel() {
    private val _header = MutableStateFlow<PlaylistHeaderUi?>(null)
    val header: StateFlow<PlaylistHeaderUi?> = _header

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    fun load() = viewModelScope.launch {
        val pl = interactor.getById(playlistId) ?: return@launch
        val tracks = interactor.getTracksByIds(pl.trackIds)
        _tracks.value = tracks
        val minutes = (tracks.sumOf { it.trackTimeMillis } / 1000 / 60).toInt()
        _header.value = PlaylistHeaderUi(
            coverPath = pl.coverPath,
            title = pl.name,
            description = pl.description,
            minutes = minutes,
            count = tracks.size
        )
    }

    fun removeTrack(trackId: Long) = viewModelScope.launch {
        interactor.removeTrackFromPlaylist(playlistId, trackId)
        load()
    }

    fun buildShareText(): String? {
        val h = header.value ?: return null
        val list = tracks.value
        if (list.isEmpty()) return null

        val sb = StringBuilder()
        sb.appendLine(h.title)
        if (!h.description.isNullOrBlank()) sb.appendLine(h.description)
        sb.appendLine("${h.count} ${pluralize(h.count)}")

        list.forEachIndexed { i, t ->
            sb.appendLine("${i + 1}. ${t.artistName} - ${t.trackName} (${t.getFormattedTrackTime()})")
        }
        return sb.toString().trim()
    }

    private fun pluralize(c: Int): String {
        val r100 = c % 100
        val r10 = c % 10
        return when {
            r100 in 11..19 -> "треков"
            r10 == 1 -> "трек"
            r10 in 2..4 -> "трека"
            else -> "треков"
        }
    }

    fun deleteCurrentPlaylist() = viewModelScope.launch {
        interactor.deletePlaylist(playlistId)
    }
}