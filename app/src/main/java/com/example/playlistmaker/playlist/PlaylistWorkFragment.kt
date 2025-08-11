package com.example.playlistmaker.playlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.RootActivity
import com.example.playlistmaker.playlist.ui.adapter.TracksInPlaylistAdapter
import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistWorkViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistWorkFragment : Fragment(R.layout.fragment_playlist_work) {

    private val playlistId: Long by lazy { requireArguments().getLong("playlistId") }
    private val vm: PlaylistWorkViewModel by viewModel { parametersOf(playlistId) }
    private lateinit var adapter: TracksInPlaylistAdapter
    private lateinit var menuBehavior: BottomSheetBehavior<View>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val overlay = view.findViewById<View>(R.id.overlay)
        view.findViewById<ImageButton>(R.id.backButton)
            .setOnClickListener { findNavController().navigateUp() }

        val sheet = view.findViewById<View>(R.id.playlists_bottom_sheet)
        BottomSheetBehavior.from(sheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
        }

        val menuSheet = view.findViewById<View>(R.id.menu_bottom_sheet)
        menuBehavior = BottomSheetBehavior.from(menuSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
        }

        fun showScrim(show: Boolean, alpha: Float = 0.8f) {
            overlay.visibility = if (show) View.VISIBLE else View.GONE
            overlay.alpha = if (show) alpha else 0f
        }

        val rv = view.findViewById<RecyclerView>(R.id.rvPlaylists)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = TracksInPlaylistAdapter(
            onClick = { track -> openPlayer(track) },
            onLongClick = { track -> confirmDelete(track)}
        )
        rv.adapter = adapter

        vm.load()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.tracks.collect { list ->
                adapter.submitList(list)
            }
        }

        menuBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                showScrim(newState != BottomSheetBehavior.STATE_HIDDEN, 0.8f)
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset >= 0f) showScrim(true, 0.4f + 0.4f * slideOffset)
            }
        })

        overlay.setOnClickListener { menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN }

        val ivCover = view.findViewById<ImageView>(R.id.zaglyshka)
        val tvTitle = view.findViewById<TextView>(R.id.PlaylistName)
        val tvDesc  = view.findViewById<TextView>(R.id.PlaylistDescription)
        val tvTime  = view.findViewById<TextView>(R.id.totalTime)
        val tvCount = view.findViewById<TextView>(R.id.numberOfTracks)

        view.findViewById<ImageButton>(R.id.menuPlaylist).setOnClickListener {
            bindMenuHeader(menuSheet)
            menuBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        view.findViewById<ImageButton>(R.id.sharePlaylist).setOnClickListener {
            shareFromMenu()
        }

        menuSheet.findViewById<TextView>(R.id.actionShare).setOnClickListener {
            shareFromMenu()
        }
        menuSheet.findViewById<TextView>(R.id.actionEdit).setOnClickListener {
            // пока заглушка, сделаем на шаге 5
            Toast.makeText(requireContext(),"Редактирование позже", Toast.LENGTH_SHORT).show()
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        menuSheet.findViewById<TextView>(R.id.actionDelete).setOnClickListener {
            confirmDeletePlaylist()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            vm.header.collect { h ->
                if (h == null) return@collect
                tvTitle.text = h.title
                tvDesc.apply {
                    text = h.description ?: ""
                    visibility = if (h.description.isNullOrBlank()) View.GONE else View.VISIBLE
                }
                tvTime.text = "${h.minutes} минут"
                tvCount.text = formatTracksCount(h.count)

                // обложка или плейсхолдер
                val path = h.coverPath
                if (path.isNullOrBlank()) {
                    ivCover.setImageResource(R.drawable.zaglyshka)
                } else {
                    val f = File(path)
                    if (f.exists()) ivCover.setImageURI(Uri.fromFile(f))
                    else ivCover.setImageResource(R.drawable.zaglyshka)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as? RootActivity)?.setBottomNavVisible(false)
    }

    override fun onPause() {
        (activity as? RootActivity)?.setBottomNavVisible(true)
        super.onPause()
    }

    private fun formatTracksCount(c: Int): String {
        val rem100 = c % 100
        val rem10 = c % 10
        val word = when {
            rem100 in 11..19 -> "треков"
            rem10 == 1 -> "трек"
            rem10 in 2..4 -> "трека"
            else -> "треков"
        }
        return "$c $word"
    }

    private fun openPlayer(track: Track) {
        val b = Bundle().apply { putSerializable("track", track) }
        findNavController().navigate(R.id.playerFragment, b)
    }

    private fun confirmDelete(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Хотите удалить трек?")
            .setNegativeButton("НЕТ", null)
            .setPositiveButton("ДА") { _, _ ->
                vm.removeTrack(track.trackId.toLong())
                Toast.makeText(requireContext(), "Трек удалён", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun shareFromMenu() {
        val text = vm.buildShareText()
        if (text.isNullOrBlank()) {
            Toast.makeText(requireContext(),"В этом плейлисте нет списка треков, которым можно поделиться",Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        startActivity(Intent.createChooser(intent, getString(R.string.share)))
    }

    private fun bindMenuHeader(menuSheet: View) {
        val iv = menuSheet.findViewById<ImageView>(R.id.ivCover)
        val title = menuSheet.findViewById<TextView>(R.id.tvTitle)
        val subtitle = menuSheet.findViewById<TextView>(R.id.tvSubtitle)

        val h = vm.header.value ?: return
        title.text = h.title
        subtitle.text = formatTracksCount(h.count)

        val path = h.coverPath
        if (path.isNullOrBlank()) {
            iv.setImageResource(R.drawable.zaglyshka)
        } else {
            val f = File(path)
            if (f.exists()) iv.setImageURI(Uri.fromFile(f))
            else iv.setImageResource(R.drawable.zaglyshka)
        }
    }

    private fun confirmDeletePlaylist() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Хотите удалить плейлист?")
            .setNegativeButton("Нет", null)
            .setPositiveButton("Да") { _, _ ->
                menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                lifecycleScope.launch {
                    vm.deleteCurrentPlaylist() // см. ниже
                    Toast.makeText(requireContext(),"Плейлист удалён",Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // вернёмся в Медиатеку
                }
            }
            .show()
    }

}