package com.example.playlistmaker.search.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var searchJob: Job? = null
    private var clickJob: Job? = null
    private var textWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter(
            tracks = emptyList(),
            onItemClick = { track ->
            clickJob?.cancel()
            clickJob = lifecycleScope.launch {
                delay(300)
                viewModel.saveTrack(track)
                findNavController()
                    .navigate(R.id.action_search_to_player, Bundle().apply {
                        putSerializable("track", track)
                    })
            }
        }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SearchFragment.adapter
        }

        historyAdapter = TrackAdapter(
            tracks = emptyList(),
            onItemClick = { track ->
            clickJob?.cancel()
            clickJob = lifecycleScope.launch {
                delay(300)
                viewModel.saveTrack(track)
                findNavController()
                    .navigate(R.id.action_search_to_player, Bundle().apply {
                        putSerializable("track", track)
                    })
            }
        }
        )
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                binding.clearButton.visibility =
                    if (query.isNotEmpty()) View.VISIBLE else View.GONE

                binding.progressBar.visibility = View.GONE
                binding.emptyPlaceholder.visibility = View.GONE
                binding.errorPlaceholder.visibility = View.GONE

                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    if (query.isEmpty()) {
                        viewModel.loadHistory()
                    } else {
                        delay(2000)
                        if (!isNetworkAvailable()) {
                            binding.historyContainer.visibility  = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.errorPlaceholder.visibility  = View.VISIBLE
                        } else {
                            viewModel.search(query)
                        }
                    }
                }
            }
        }
        binding.searchEditText.addTextChangedListener(textWatcher)

        binding.clearButton.setOnClickListener {
            binding.searchEditText.text?.clear()
            binding.errorPlaceholder.visibility = View.GONE
            viewModel.loadHistory()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.refreshButton.setOnClickListener {
            val q = binding.searchEditText.text.toString()
            if (q.isEmpty()) viewModel.loadHistory() else viewModel.search(q)
        }

        restoreSearchState()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            binding.errorPlaceholder.visibility = if (state.isError) View.VISIBLE else View.GONE

            val hasQuery  = state.query.isNotEmpty()
            val noResults = state.tracks.isEmpty()

            binding.emptyPlaceholder.visibility =
                if (hasQuery && noResults && !state.isLoading && !state.isError)
                    View.VISIBLE else View.GONE

            if (state.query.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                if (state.showHistory && state.history.isNotEmpty()) {
                    binding.historyContainer.visibility = View.VISIBLE
                    historyAdapter.updateTracks(state.history)
                } else {
                    binding.historyContainer.visibility = View.GONE
                }
            } else {
                binding.historyContainer.visibility = View.GONE
                if (noResults) {
                    binding.recyclerView.visibility = View.GONE
                } else {
                    adapter.updateTracks(state.tracks)
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }

            binding.clearHistoryButton.visibility =
                if (state.showHistory) View.VISIBLE else View.GONE
        }
    }

    private fun restoreSearchState() {
        val last = viewModel.state.value ?: return
        binding.searchEditText.setText(last.query)
        if (last.query.isEmpty()) viewModel.loadHistory()
        else viewModel.search(last.query)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding.searchEditText.removeTextChangedListener(it) }
        searchJob?.cancel()
        clickJob?.cancel()
        _binding = null
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = requireContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}