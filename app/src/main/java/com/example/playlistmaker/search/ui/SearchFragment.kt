package com.example.playlistmaker.search.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import com.example.playlistmaker.search.ui.viewmodel.SearchState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import com.example.playlistmaker.R

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.state.observeAsState(SearchState())
                var forceEmptyResults by remember { mutableStateOf(false) }
                var localNoNetworkError by remember { mutableStateOf(false) }

                SearchScreen(
                    state = state,
                    localNoNetworkError = localNoNetworkError,
                    forceEmptyResults = forceEmptyResults,

                    onTextChangedSideEffects = {
                        localNoNetworkError = false
                        forceEmptyResults = false
                    },

                    onDebouncedQuery = { q ->
                        if (q.isEmpty()) {
                            viewModel.loadHistory()
                        } else {
                            if (!isNetworkAvailable(requireContext())) {
                                localNoNetworkError = true
                            } else {
                                viewModel.search(q)
                            }
                        }
                    },

                    onClearButton = {
                        localNoNetworkError = false
                        forceEmptyResults = true
                        viewModel.loadHistory()
                    },

                    onRefresh = { currentText ->
                        if (currentText.isEmpty()) {
                            forceEmptyResults = true
                            localNoNetworkError = false
                            viewModel.loadHistory()
                        } else if (!isNetworkAvailable(requireContext())) {
                            localNoNetworkError = true
                        } else {
                            localNoNetworkError = false
                            viewModel.search(currentText)
                        }
                    },

                    onClearHistory = {
                        forceEmptyResults = true
                        localNoNetworkError = false
                        viewModel.clearHistory()
                    },

                    onTrackClick = { track ->
                        lifecycleScope.launch {
                            delay(300)
                            viewModel.saveTrack(track)
                            findNavController().navigate(
                                R.id.action_search_to_player,
                                Bundle().apply { putSerializable("track", track) }
                            )
                        }
                    },

                    onRestore = {
                        val last = viewModel.state.value
                        if (last == null || last.query.isEmpty()) {
                            forceEmptyResults = false
                            viewModel.loadHistory()
                        } else {
                            forceEmptyResults = false
                            viewModel.search(last.query)
                        }
                    }
                )
            }
        }

    private fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}