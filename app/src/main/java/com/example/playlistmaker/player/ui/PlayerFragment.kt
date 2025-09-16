package com.example.playlistmaker.player.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.RootActivity
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.service.AudioPlayerService
import com.example.playlistmaker.player.ui.PlaylistsBottomSheetAdapter.PlaylistsBottomSheetAdapter
import com.example.playlistmaker.player.ui.viewmodel.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment(R.layout.fragment_player) {

    companion object {
        private const val ARG_TRACK = "track"
        fun newInstance(track: Track): PlayerFragment {
            return PlayerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TRACK, track)
                }
            }
        }
    }

    private val requestNotifPermission = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && serviceBound && lastIsPlaying) {
            audioService?.showNotification()
        }
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val initialTrack: Track by lazy {
        requireArguments().getSerializable(ARG_TRACK) as Track
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var sheetAdapter: PlaylistsBottomSheetAdapter
    private val viewModel: PlayerViewModel by viewModel { parametersOf(initialTrack) }

    private fun navControllerFromActivity(): NavController? {
        val fm = requireActivity().supportFragmentManager
        val host = fm.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        return host?.navController
    }
    private var audioService: AudioPlayerService? = null
    private var serviceBound = false
    private var lastIsPlaying: Boolean = false
    private var uiInBackground = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPlayerBinding.bind(view)
        viewModel.prepare(initialTrack)

        val navController = findNavController()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { st: PlayerState ->
                binding.trackName.text = st.track.trackName
                binding.artistName.text = st.track.artistName
                binding.time.text = formatTime(st.track.trackTimeMillis.toInt())
                binding.albumName.text = st.track.collectionName ?: "—"
                binding.releaseYear.text = st.track.releaseDate?.substring(0, 4) ?: "—"
                binding.genreTrack.text = st.track.primaryGenreName ?: "—"
                binding.countryTrack.text = st.track.country ?: "—"
                Glide.with(this@PlayerFragment)
                    .load(st.track.getCoverArtwork())
                    .into(binding.coverArtwork)
                binding.playButton.isEnabled = true
                binding.playButton.setPlaying(st.isPlaying)
                lastIsPlaying = st.isPlaying
                if (uiInBackground && !st.isPlaying) {
                    audioService?.hideNotification()
                }
                binding.currentTime.text = formatTime(st.position)
                binding.addToFavoritesButton.setImageResource(
                    if (st.isFavorite) R.drawable.button_fave_activ
                    else R.drawable.favorite_track
                )
            }
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.addToFavoritesButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        val overlay = requireView().findViewById<View>(R.id.overlay)
        val sheet = requireView().findViewById<LinearLayout>(R.id.playlists_bottom_sheet)
        val rv = requireView().findViewById<RecyclerView>(R.id.rvPlaylists)
        val btnNew = requireView().findViewById<TextView>(R.id.btnNewPlaylist)

        bottomSheetBehavior = BottomSheetBehavior.from(sheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        val scrimColor = Color.parseColor("#1A1B22")
        overlay.setBackgroundColor(scrimColor)

        val SCRIM_MIN = 0.40f
        val SCRIM_MAX = 0.80f

        fun showScrim(a: Float) {
            val x = a.coerceIn(0f, 1f)
            overlay.visibility = if (x == 0f) View.GONE else View.VISIBLE
            overlay.alpha = x
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> showScrim(0f)
                    BottomSheetBehavior.STATE_COLLAPSED -> showScrim(SCRIM_MIN)
                    BottomSheetBehavior.STATE_HALF_EXPANDED,
                    BottomSheetBehavior.STATE_EXPANDED -> showScrim(SCRIM_MAX)
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val p = slideOffset.coerceIn(0f, 1f)
                showScrim(SCRIM_MIN + (SCRIM_MAX - SCRIM_MIN) * p)
            }
        })
        sheetAdapter = PlaylistsBottomSheetAdapter { playlist ->
            viewModel.onAddCurrentTrackTo(playlist)
        }
        rv.adapter = sheetAdapter

        rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.playlists.collect { list ->
                sheetAdapter.submitList(list)
            }
        }

        binding.createAlbumButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        btnNew.setOnClickListener {
            val callBack = object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        bottomSheetBehavior.removeBottomSheetCallback(this)
                        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                            findNavController().navigate(R.id.createPlaylistFragment)
                        }
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            }
            bottomSheetBehavior.addBottomSheetCallback(callBack)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playlists.collectLatest { list ->
                sheetAdapter.submitList(list)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addResult.collectLatest { result ->
                when (result) {
                    is PlayerViewModel.AddResult.Added -> {
                        Toast.makeText(requireContext(), "Добавлено в плейлист ${result.playlistName}", Toast.LENGTH_SHORT).show()
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                    is PlayerViewModel.AddResult.AlreadyThere -> {
                        Toast.makeText(requireContext(), "Трек уже добавлен в плейлист ${result.playlistName}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.playButton.setOnToggleListener {
            viewModel.onPlayPauseClicked()
        }

        val intent = android.content.Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra(AudioPlayerService.EXTRA_URL, initialTrack.previewUrl ?: "")
            putExtra(AudioPlayerService.EXTRA_ARTIST, initialTrack.artistName)
            putExtra(AudioPlayerService.EXTRA_TITLE, initialTrack.trackName)
        }
        requireContext().bindService(intent, serviceConnection, android.content.Context.BIND_AUTO_CREATE)

    }

    private fun formatTime(ms: Int): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        viewModel.onScreenClosed()
        if (serviceBound) {
            requireContext().unbindService(serviceConnection)
            serviceBound = false
        }
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        (activity as? RootActivity)?.setBottomNavVisible(false)
    }

    override fun onPause() {
        (activity as? RootActivity)?.setBottomNavVisible(true)
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onUiVisible()
    }

    override fun onStop() {
        super.onStop()
        ensureNotifPermissionIfNeeded()
        viewModel.onUiHidden()
    }

    private val serviceConnection = object : android.content.ServiceConnection {
        override fun onServiceConnected(
            name: android.content.ComponentName?, binder: android.os.IBinder?
        ) {
            val service = (binder as AudioPlayerService.AudioBinder).getService()
            audioService = service
            serviceBound = true
            viewModel.attachService(service)
            service.prepare(
                initialTrack.previewUrl ?: "",
                initialTrack.artistName,
                initialTrack.trackName
            )

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    service.state().collect { st ->
                        val playing = (st == AudioPlayerService.ServicePlayerState.Playing)
                        binding.playButton.setPlaying(playing)
                        lastIsPlaying = playing
                        if (st == AudioPlayerService.ServicePlayerState.Completed ||
                            st == AudioPlayerService.ServicePlayerState.Idle
                        ) {
                            binding.currentTime.text = "00:00"
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    service.progressMs().collect { ms ->
                        binding.currentTime.text = formatMs(ms)
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: android.content.ComponentName?) {
            serviceBound = false
            audioService = null
            viewModel.detachService()
        }
    }

    private fun ensureNotifPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            val perm = android.Manifest.permission.POST_NOTIFICATIONS
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    requireContext(), perm
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestNotifPermission.launch(perm)
            }
        }
    }

    private fun formatMs(ms: Long): String {
        val total = ms / 1000
        val m = total / 60
        val s = total % 60
        return "%02d:%02d".format(m, s)
    }

}