package com.example.playlistmaker.media.ui.Fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.RootActivity
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.ui.viewmodel.CreatePlaylistViewModel
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment(R.layout.fragment_create_playlist) {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private var selectedCoverUri: Uri? = null
    private val viewModel: CreatePlaylistViewModel by viewModel()

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedCoverUri = uri
            val radius = resources.getDimensionPixelSize(R.dimen.cover_radius)
            Glide.with(binding.ivCoverPlaceholder)
                .load(uri)
                .transform(CenterCrop(), RoundedCorners(radius))
                .placeholder(R.drawable.zaglyshka)
                .error(R.drawable.zaglyshka)
                .into(binding.ivCoverPlaceholder)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCoverUri = savedInstanceState?.getParcelable("cover_uri")
    }

    private fun TextInputLayout.keepActiveWhenFilled(
        edit: TextInputEditText,
        activeColor: Int,
        normalColor: Int,
        normalHintColor: Int,
        activeHintColor: Int = activeColor
    ) {
        fun stateList(color: Int) = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_hovered),
                intArrayOf()
            ),
            intArrayOf(color, color, color, color)
        )

        fun apply() {
            val filled = !edit.text.isNullOrBlank()
            val stroke = if (filled) stateList(activeColor) else stateList(normalColor)
            val hint = if (filled) stateList(activeHintColor) else stateList(normalHintColor)

            try {
                setBoxStrokeColorStateList(stroke)
            } catch (_: NoSuchMethodError) {
                boxStrokeColor = stroke.defaultColor
            }
            setDefaultHintTextColor(hint)
            hintTextColor = hint
        }

        edit.doAfterTextChanged { apply() }
        edit.setOnFocusChangeListener { _, _ -> apply() }
        apply()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreatePlaylistBinding.bind(view)

        selectedCoverUri?.let { uri ->
            val radius = resources.getDimensionPixelSize(R.dimen.cover_radius)
            Glide.with(binding.ivCoverPlaceholder)
                .load(uri)
                .transform(CenterCrop(), RoundedCorners(radius))
                .placeholder(R.drawable.zaglyshka)
                .error(R.drawable.zaglyshka)
                .into(binding.ivCoverPlaceholder)
        }

        binding.backButton.setOnClickListener { attemptExit() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = attemptExit()
            }
        )

        fun updateCreateEnabled() {
            val hasTitle = binding.etPlaylistName.text?.toString()?.trim()?.isNotEmpty() == true
            binding.btnCreatePlaylist.isEnabled = hasTitle
        }

        updateCreateEnabled()
        binding.etPlaylistName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateCreateEnabled()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnCreatePlaylist.setOnClickListener {
            val name = binding.etPlaylistName.text?.toString()?.trim().orEmpty()
            val description = binding.etPlaylistDescription.text?.toString()?.trim().orEmpty()
            lifecycleScope.launch {
                val coverPath = selectedCoverUri?.let { uri -> copyImageToAppStorage(uri) }
                viewModel.create(
                    name = name,
                    description = description.ifBlank { null },
                    coverPath = coverPath,
                    onDone = {
                        Toast.makeText(requireContext(), "Плейлист «$name» создан", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    },
                    onError = {
                        Toast.makeText(requireContext(), "Ошибка сохранения плейлиста", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        binding.ivCoverPlaceholder.setOnClickListener {
            pickImage.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.tilPlaylistName.keepActiveWhenFilled(
            binding.etPlaylistName,
            activeColor = ContextCompat.getColor(requireContext(), R.color.blue_creat),
            normalColor = ContextCompat.getColor(requireContext(), R.color.color_gray),
            normalHintColor = ContextCompat.getColor(requireContext(),R.color.black)
        )

        binding.tilPlaylistDescription.keepActiveWhenFilled(
            binding.etPlaylistDescription,
            activeColor = ContextCompat.getColor(requireContext(), R.color.blue_creat),
            normalColor = ContextCompat.getColor(requireContext(), R.color.color_gray),
            normalHintColor = ContextCompat.getColor(requireContext(), R.color.black)
        )

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedCoverUri?.let { outState.putParcelable("cover_uri", it) }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private suspend fun copyImageToAppStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val resolver = requireContext().contentResolver
            val mime = resolver.getType(uri)
            val ext = when (mime) {
                "image/png" -> "png"
                "image/webp" -> "webp"
                "image/heic" -> "heic"
                "image/heif" -> "heif"
                else -> "jpg"
            }

            val dir = File(requireContext().filesDir, "playlists")
            if (!dir.exists()) dir.mkdirs()

            val file = File(dir, "cover_${System.currentTimeMillis()}.$ext")

            resolver.openInputStream(uri).use { input ->
                if (input == null) return@withContext null
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun attemptExit() {
        if (hasUnsavedChanges()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_exit_title)
                .setMessage(R.string.dialog_exit_message)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_finish) { _, _ ->
                    findNavController().popBackStack()
                }
                .show()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        val titleNotEmpty = binding.etPlaylistName.text?.toString()?.trim()?.isNotEmpty() == true
        val descNotEmpty = binding.etPlaylistDescription.text?.toString()?.trim()?.isNotEmpty() == true
        val coverChosen = selectedCoverUri != null
        return titleNotEmpty || descNotEmpty || coverChosen
    }

    override fun onResume() {
        super.onResume()
        (activity as? RootActivity)?.setBottomNavVisible(false)
    }

    override fun onPause() {
        (activity as? RootActivity)?.setBottomNavVisible(true)
        super.onPause()
    }


}