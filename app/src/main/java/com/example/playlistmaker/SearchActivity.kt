package com.example.playlistmaker

import SearchHistory
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.RetrofitInstance
import model.Track
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import kotlin.coroutines.cancellation.CancellationException

class SearchActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: TrackAdapter
    private var tracks: List<Track> = emptyList()
    private lateinit var searchHistory: SearchHistory
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        initViews()
        setupRecyclerView()
        setupListeners()
        searchHistory = SearchHistory(this)

        if (savedInstanceState != null) {
            searchEditText.setText(savedInstanceState.getString("search_query"))
        }
    }

    private fun initViews() {
        backButton = findViewById(R.id.back)
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(tracks) { track ->
            searchHistory.addTrack(track)
            startPlayerActivity(track)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun startPlayerActivity(track: Track) {
        Intent(this, PlayerActivity::class.java).apply {
            putExtra("track", track)
            startActivity(this)
        }
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            clearSearchResults()
        }

        searchEditText.doAfterTextChanged { text ->
            updateClearButtonVisibility(text)
            updateHistoryVisibility()
            performSearchDebounced(text.toString())
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchEditText.text?.toString()?.let { performSearchDebounced(it) }
                true
            } else false
        }

        findViewById<TextView>(R.id.refresh_button).setOnClickListener {
            searchEditText.text?.toString()?.takeIf { it.isNotEmpty() }?.let {
                performSearchDebounced(it)
            }
        }

        findViewById<TextView>(R.id.clearHistoryButton).setOnClickListener {
            searchHistory.clearHistory()
            updateHistoryVisibility()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                updateHistoryVisibility()
            } else {
                findViewById<LinearLayout>(R.id.historyContainer).visibility = View.GONE
            }
        }
    }

    private fun performSearchDebounced(query: String) {
        searchJob?.cancel()
        searchJob = null
        searchJob = lifecycleScope.launch {
            if (query.isEmpty()) {
                clearSearchResults()
                return@launch
            }

            try {
                delay(2000)
                if (!isActive) return@launch

                performSearch(query)
            } catch (e: CancellationException) {
            }
        }
    }

    private suspend fun performSearch(query: String) {
        withContext(Dispatchers.Main) {
            findViewById<LinearLayout>(R.id.historyContainer).visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }

        try {
            val response = RetrofitInstance.api.search(query).awaitResponse()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        tracks = apiResponse.results.mapNotNull { apiTrack ->
                            Track(
                                trackName = apiTrack.trackName ?: return@mapNotNull null,
                                artistName = apiTrack.artistName ?: return@mapNotNull null,
                                trackTimeMillis = apiTrack.trackTimeMillis ?: 0,
                                artworkUrl100 = apiTrack.artworkUrl100 ?: "",
                                collectionName = apiTrack.collectionName,
                                releaseDate = apiTrack.releaseDate,
                                primaryGenreName = apiTrack.primaryGenreName,
                                country = apiTrack.country,
                                previewUrl = apiTrack.previewUrl ?: ""
                            )
                        }
                        if (tracks.isEmpty()) {
                            showEmptyPlaceholder()
                        } else {
                            showResults(tracks)
                        }
                    } ?: showErrorPlaceholder()
                } else {
                    showErrorPlaceholder()
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showErrorPlaceholder()
            }
        } finally {
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun clearSearchResults() {
        tracks = emptyList()
        adapter.updateTracks(emptyList())
        hidePlaceholders()
        findViewById<LinearLayout>(R.id.emptyPlaceholder).visibility = View.GONE
    }

    private fun showResults(tracks: List<Track>) {
        adapter.updateTracks(tracks)
        recyclerView.visibility = View.VISIBLE
        hidePlaceholders()
    }

    private fun showEmptyPlaceholder() {
        findViewById<LinearLayout>(R.id.emptyPlaceholder).visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        findViewById<LinearLayout>(R.id.errorPlaceholder).visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        findViewById<LinearLayout>(R.id.errorPlaceholder).visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        findViewById<LinearLayout>(R.id.emptyPlaceholder).visibility = View.GONE
    }

    private fun hidePlaceholders() {
        findViewById<LinearLayout>(R.id.emptyPlaceholder).visibility = View.GONE
        findViewById<LinearLayout>(R.id.errorPlaceholder).visibility = View.GONE
    }

    private fun updateClearButtonVisibility(text: CharSequence?) {
        clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateHistoryVisibility() {
        val history = searchHistory.getHistory()
        val historyContainer = findViewById<LinearLayout>(R.id.historyContainer)
        val historyRecyclerView = findViewById<RecyclerView>(R.id.historyRecyclerView)
        val clearHistoryButton = findViewById<TextView>(R.id.clearHistoryButton)

        if (searchEditText.text.isEmpty() && searchEditText.hasFocus() && history.isNotEmpty()) {
            historyContainer.visibility = View.VISIBLE
            historyRecyclerView.layoutManager = LinearLayoutManager(this)
            historyRecyclerView.adapter = TrackAdapter(history) { track ->
                searchHistory.addTrack(track)
                startPlayerActivity(track)
            }
            clearHistoryButton.visibility = View.VISIBLE
        } else {
            historyContainer.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search_query", searchEditText.text.toString())
    }

}
