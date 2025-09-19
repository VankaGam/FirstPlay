package com.example.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import kotlinx.coroutines.isActive
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AudioPlayerService : Service(), AudioPlayerBar {

    enum class ServicePlayerState { Idle, Preparing, Playing, Paused, Completed, Error }

    private var callback: AudioPlayerBar.PlayerStateListener? = null
    override fun setPlayerStateListener(listener: AudioPlayerBar.PlayerStateListener?) {
        callback = listener
    }
    private fun notifyCallback() {
        callback?.onStateChanged(_state.value, _progressMs.value)
    }
    private var mediaPlayer: android.media.MediaPlayer? = null
    private val scope = kotlinx.coroutines.CoroutineScope(
        kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.Main.immediate
    )
    private val _state = kotlinx.coroutines.flow.MutableStateFlow(ServicePlayerState.Idle)
    private val _progressMs = kotlinx.coroutines.flow.MutableStateFlow(0L)
    private var progressJob: kotlinx.coroutines.Job? = null

    override fun state() = _state
    override fun progressMs() = _progressMs

    private var foregroundActive = false

    companion object {
        const val EXTRA_URL = "extra_url"
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_TITLE = "extra_title"
        const val CHANNEL_ID = "pm_playback"
        const val NOTIF_ID = 101
    }

    inner class AudioBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }
    private val binder = AudioBinder()

    private var url: String = ""
    private var artist: String = ""
    private var title: String = ""
    private var playing = false
    private var prepared = false

    override fun onCreate() {
        super.onCreate()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW
                ).apply { setSound(null, null) }
            )
        }
        mediaPlayer = android.media.MediaPlayer()
    }

    override fun onBind(intent: Intent): IBinder {
        intent.getStringExtra(EXTRA_URL)?.let { url = it }
        intent.getStringExtra(EXTRA_ARTIST)?.let { artist = it }
        intent.getStringExtra(EXTRA_TITLE)?.let { title = it }
        return binder
    }

    override fun onDestroy() {
        stopProgress()
        mediaPlayer?.release()
        mediaPlayer = null
        scope.cancel()
        super.onDestroy()
    }

    override fun prepare(url: String, artist: String, title: String) {
        this.url = url; this.artist = artist; this.title = title
        mediaPlayer?.release()
        mediaPlayer = android.media.MediaPlayer().apply {
            setOnPreparedListener {
                _state.value = ServicePlayerState.Paused
                _progressMs.value = 0L
                notifyCallback()
            }
            setOnCompletionListener {
                _state.value = ServicePlayerState.Completed
                _progressMs.value = 0L
                hideNotification()
                stopProgress()
                notifyCallback()
            }
            setOnErrorListener { _, _, _ ->
                _state.value = ServicePlayerState.Error
                stopProgress()
                notifyCallback()
                false
            }
            _state.value = ServicePlayerState.Preparing
            notifyCallback()
            setDataSource(url)
            prepareAsync()
        }
    }

    override fun play() {
        mediaPlayer?.start() ?: return
        _state.value = ServicePlayerState.Playing
        notifyCallback()
        startProgress()
    }

    override fun pause() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
        _state.value = ServicePlayerState.Paused
        notifyCallback()
        stopProgress()
    }

    override fun stop() {
        mediaPlayer?.let { if (it.isPlaying) it.stop() }
        _state.value = ServicePlayerState.Idle
        _progressMs.value = 0L
        notifyCallback()
        stopProgress()
        hideNotification()
    }

    override fun isPlaying(): Boolean = _state.value == ServicePlayerState.Playing

    private fun startProgress() {
        stopProgress()
        progressJob = scope.launch {
            val mp = mediaPlayer ?: return@launch
            while (isActive && mp.isPlaying) {
                _progressMs.value = mp.currentPosition.toLong()
                notifyCallback()
                kotlinx.coroutines.delay(300)
            }
        }
    }
    private fun stopProgress() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun showNotification() {
        if (_state.value != ServicePlayerState.Playing) return

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            val granted = androidx.core.content.ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }
        val notification = buildNotification(artist, title)
        ServiceCompat.startForeground(
            this,
            NOTIF_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
        foregroundActive = true
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(NOTIF_ID)
        foregroundActive = false
    }

    private fun buildNotification(artist: String, title: String): Notification {
        val content = if (artist.isNotBlank() || title.isNotBlank()) "$artist - $title" else ""
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Playlist Maker")
            .setContentText(content)
            .setOngoing(true)
            .build()
    }
}