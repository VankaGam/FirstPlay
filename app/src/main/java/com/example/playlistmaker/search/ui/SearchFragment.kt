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

        adapter = TrackAdapter(emptyList()) { track ->
            clickJob?.cancel()
            clickJob = lifecycleScope.launch {
                delay(300)
                viewModel.saveTrack(track)
                val bundle = Bundle().apply { putSerializable("track", track) }
                findNavController().navigate(R.id.action_search_to_player, bundle)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        historyAdapter = TrackAdapter(emptyList()) { track ->
            clickJob?.cancel()
            clickJob = lifecycleScope.launch {
                delay(300)
                viewModel.saveTrack(track)
                val bundle = Bundle().apply { putSerializable("track", track) }
                findNavController().navigate(R.id.action_search_to_player, bundle)
            }
        }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = historyAdapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                binding.clearButton.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                binding.progressBar.visibility = View.GONE
                binding.emptyPlaceholder.visibility = View.GONE
                binding.errorPlaceholder.visibility = View.GONE
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    if (query.isEmpty()) {
                        viewModel.loadHistory()
                    } else {
                        delay(500)
                        if (!isNetworkAvailable()) {
                            binding.historyContainer.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.errorPlaceholder.visibility = View.VISIBLE
                        } else {
                            binding.errorPlaceholder.visibility = View.GONE
                            viewModel.search(query)
                        }
                    }
                }
            }
        }
        binding.searchEditText.addTextChangedListener(textWatcher)

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                binding.errorPlaceholder.visibility = View.GONE
                viewModel.loadHistory()
            }
        }

        binding.clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
            binding.errorPlaceholder.visibility = View.GONE
            viewModel.loadHistory()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            binding.errorPlaceholder.visibility = if (state.isError) View.VISIBLE else View.GONE

            val queryNotEmpty = state.query.isNotEmpty()
            val noResults = state.tracks.isEmpty()

            binding.emptyPlaceholder.visibility =
                if (queryNotEmpty && noResults && !state.isLoading && !state.isError)
                    View.VISIBLE
                else
                    View.GONE

            if (state.query.isEmpty()) {
                binding.emptyPlaceholder.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE

                if (state.showHistory && state.history.isNotEmpty()) {
                    binding.historyContainer.visibility = View.VISIBLE
                    historyAdapter.updateTracks(state.history)
                } else {
                    binding.historyContainer.visibility = View.GONE
                    historyAdapter.updateTracks(emptyList())
                }

            } else {
                binding.historyContainer.visibility = View.GONE

                if (noResults) {
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.emptyPlaceholder.visibility = View.GONE
                    adapter.updateTracks(state.tracks)
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }
        }

        viewModel.loadHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding.searchEditText.removeTextChangedListener(it) }
        searchJob?.cancel()
        clickJob?.cancel()
        _binding = null
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}