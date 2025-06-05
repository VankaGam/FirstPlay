package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        fun newInstance(): SearchFragment = SearchFragment()
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private var textWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter(emptyList()) { track: Track ->
            viewModel.saveTrack(track)
            val bundle = Bundle().apply {
                putSerializable("track", track)
            }
            findNavController().navigate(
                R.id.action_search_to_player,
                bundle
            )
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        historyAdapter = TrackAdapter(emptyList()) { track: Track ->
            viewModel.saveTrack(track)
            val bundle = Bundle().apply {
                putSerializable("track", track)
            }
            findNavController().navigate(
                R.id.action_search_to_player,
                bundle
            )
        }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = historyAdapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                binding.clearButton.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                if (query.isEmpty()) {
                    viewModel.loadHistory()
                } else {
                    viewModel.search(query)
                }
            }
        }
        binding.searchEditText.addTextChangedListener(textWatcher)

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                viewModel.loadHistory()
            }
        }

        binding.clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
            viewModel.loadHistory()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            binding.errorPlaceholder.visibility = if (state.isError) View.VISIBLE else View.GONE
            binding.emptyPlaceholder.visibility = if (state.isEmpty) View.VISIBLE else View.GONE

            if (state.query.isEmpty() && state.showHistory) {
                binding.historyContainer.visibility = View.VISIBLE
                historyAdapter.updateTracks(state.history)
                binding.recyclerView.visibility = View.GONE
            } else if (state.query.isNotEmpty()) {
                binding.historyContainer.visibility = View.GONE
                adapter.updateTracks(state.tracks)
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.loadHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding.searchEditText.removeTextChangedListener(it) }
        _binding = null
    }
}