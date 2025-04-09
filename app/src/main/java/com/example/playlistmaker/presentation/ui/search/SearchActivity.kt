package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.example.playlistmaker.presentation.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.presentation.ui.viewmodel.SearchViewModelFactory
import com.example.playlistmaker.presentation.ui.search.TrackAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var emptyPlaceholder: LinearLayout
    private lateinit var historyContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: TextView


    private val viewModel: SearchViewModel by viewModels { SearchViewModelFactory(applicationContext) }
    private var searchJob: Job? = null

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val searchViewModel: SearchViewModel by viewModels {
        Creator.provideSearchViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupRecyclerViews()
        setupListeners()
        observeViewModel()

        viewModel.loadHistory()
    }

    private fun initViews() {
        backButton = findViewById(R.id.back)
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        historyContainer = findViewById(R.id.historyContainer)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
    }

    private fun setupRecyclerViews() {
        adapter = TrackAdapter(emptyList()) { track ->
            viewModel.saveTrackToHistory(track)
            openPlayer(track)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        historyAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.saveTrackToHistory(track)
            openPlayer(track)
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            viewModel.loadHistory()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                viewModel.loadHistory()
            } else {
                historyContainer.visibility = View.GONE
            }
        }

        searchEditText.doAfterTextChanged { text ->
            updateClearButtonVisibility(text)
            debounceSearch(text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.tracks.observe(this) { tracks ->
            adapter.updateTracks(tracks)
            recyclerView.visibility = View.VISIBLE
            emptyPlaceholder.visibility = View.GONE
            errorPlaceholder.visibility = View.GONE
            historyContainer.visibility = View.GONE
        }

        viewModel.history.observe(this) { history ->
            historyAdapter.updateTracks(history)
            historyContainer.visibility =
                if (searchEditText.text.isEmpty() && searchEditText.hasFocus() && history.isNotEmpty())
                    View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                errorPlaceholder.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                emptyPlaceholder.visibility = View.GONE
                historyContainer.visibility = View.GONE
            }
        }

        viewModel.isEmptyResult.observe(this) { isEmpty ->
            if (isEmpty) {
                emptyPlaceholder.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                errorPlaceholder.visibility = View.GONE
                historyContainer.visibility = View.GONE
            }
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    private fun updateClearButtonVisibility(text: CharSequence?) {
        clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun debounceSearch(query: String) {
        searchJob?.cancel()
        searchJob = null
        if (query.isNotBlank()) {
            searchJob = lifecycleScope.launch {
                delay(2000)
                viewModel.search(query)
            }
        }
    }
}