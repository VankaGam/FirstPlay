package com.example.playlistmaker

import SearchHistory
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.ApiResponse
import api.RetrofitInstance
import model.Track
import model.TrackRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private var tracks: List<Track> = emptyList()
    private var lastSearchTerm: String? = null
    private lateinit var searchHistory: SearchHistory
    private lateinit var progressBar: ProgressBar
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        initViews()
        setListeners()
        setupWindowInsets()
        searchHistory = SearchHistory(this)
        updateHistoryVisibility()
        setupRecyclerView()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(tracks) { track ->
            searchHistory.addTrack(track)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        if (savedInstanceState != null) {
            val savedQuery = savedInstanceState.getString("search_query")
            searchEditText.setText(savedQuery)
            if (!savedQuery.isNullOrEmpty()) {
                performSearchDebounced(savedQuery)
            }
        }
    }

    private fun initViews() {
        backButton = findViewById(R.id.back)
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(tracks) { track ->
            searchHistory.addTrack(track)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setListeners() {
        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            clearSearchQuery()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString()
                if (query.isNotEmpty()) {
                    performSearchDebounced(query)
                }
                true
            } else {
                false
            }
        }
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                updateHistoryVisibility()
            } else {
                findViewById<LinearLayout>(R.id.historyContainer).visibility = View.GONE
                findViewById<TextView>(R.id.clearHistoryButton).visibility = View.GONE
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateClearButtonVisibility(s)
                if (searchEditText.hasFocus()) {
                    updateHistoryVisibility()
                }
                performSearchDebounced(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        findViewById<TextView>(R.id.refresh_button).setOnClickListener {
            lastSearchTerm?.let { term ->
                performSearchDebounced(term)
            }
        }
        findViewById<TextView>(R.id.clearHistoryButton).setOnClickListener {
            searchHistory.clearHistory()
            updateHistoryVisibility()
        }
    }

    private fun performSearchDebounced(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            if (query.isNotEmpty()) {
                lastSearchTerm = query
                delay(2000) // Debounce 2 секунды

                progressBar.visibility = View.VISIBLE
                try {
                    val response = RetrofitInstance.api.search(query).awaitResponse()
                    if (response.isSuccessful && response.body() != null) {
                        val apiResponse = response.body()!!
                        tracks = apiResponse.results.map { track ->
                            Track(
                                track.trackName,
                                track.artistName,
                                track.trackTimeMillis,
                                track.artworkUrl100,
                                track.collectionName,
                                track.releaseDate,
                                track.primaryGenreName,
                                track.country
                            )
                        }
                        showResults(tracks)
                    } else {
                        showEmptyPlaceholder()
                    }
                } catch (e: Exception) {
                    showErrorPlaceholder()
                    Log.e("SearchActivity", "Network error: ${e.message}")
                } finally {
                    progressBar.visibility = View.GONE
                }
            } else {
                clearSearchResults()
            }
        }
    }

    private fun clearSearchResults() {
        tracks = emptyList()
        adapter.updateTracks(emptyList())
        findViewById<LinearLayout>(R.id.emptyPlaceholder).visibility = View.GONE
        findViewById<LinearLayout>(R.id.errorPlaceholder).visibility = View.GONE
    }

    private fun updateClearButtonVisibility(s: CharSequence?) {
        clearButton.visibility = if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clearSearchQuery() {
        searchEditText.text.clear()
        clearButton.visibility = View.GONE
        hideKeyboard()
        tracks = emptyList()
        adapter = TrackAdapter(tracks) { track ->
            searchHistory.addTrack(track)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        findViewById<LinearLayout>(R.id.emptyPlaceholder).visibility = View.GONE
        findViewById<LinearLayout>(R.id.errorPlaceholder).visibility = View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(InputMethodManager::class.java)
        inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun performSearch(term: String) {
        val apiService = RetrofitInstance.api
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }
        apiService.search(term).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.resultCount > 0) {
                        tracks = apiResponse.results.map { track ->
                            Track(
                                track.trackName,
                                track.artistName,
                                track.trackTimeMillis,
                                track.artworkUrl100,
                                track.collectionName,
                                track.releaseDate,
                                track.primaryGenreName,
                                track.country
                            )
                        }
                        showResults(tracks)
                    } else {
                        showEmptyPlaceholder()
                    }
                } else {
                    showErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showErrorPlaceholder()
                Log.e("SearchActivity", "Network error: ${t.message}")
            }
        })
    }

    private fun showResults(tracks: List<Track>) {
        Log.d("SearchActivity", "Showing results: ${tracks.size} tracks")
        adapter = TrackAdapter(tracks) { track ->
            searchHistory.addTrack(track)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.emptyPlaceholder).visibility = View.GONE
        findViewById<LinearLayout>(R.id.errorPlaceholder).visibility = View.GONE
    }

    private fun showEmptyPlaceholder() {
        findViewById<LinearLayout>(R.id.emptyPlaceholder).visibility = View.VISIBLE
        findViewById<RecyclerView>(R.id.recyclerView).visibility = View.GONE
        findViewById<LinearLayout>(R.id.errorPlaceholder).visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        findViewById<LinearLayout>(R.id.errorPlaceholder).visibility = View.VISIBLE
        findViewById<RecyclerView>(R.id.recyclerView).visibility = View.GONE
        findViewById<LinearLayout>(R.id.emptyPlaceholder).visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search_query", searchEditText.text.toString())
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("track", track)
                startActivity(intent)
            }
            clearHistoryButton.visibility = View.VISIBLE
        } else {
            historyContainer.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }
    }
}
