package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyPlaceholder: LinearLayout
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var historyContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: TextView
    private lateinit var viewModel: SearchViewModel

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var lastQuery = ""


    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = Creator.provideSearchViewModel(this)

        initViews()
        setupRecycler()
        setupListeners()

        viewModel.loadHistory()
        updateHistory()

        savedInstanceState?.getString("search_query")?.let {
            searchEditText.setText(it)
        }
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        historyContainer = findViewById(R.id.historyContainer)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        findViewById<ImageButton>(R.id.back).setOnClickListener { finish() }
    }

    private fun setupRecycler() {
        adapter = TrackAdapter(emptyList()) {
            viewModel.saveTrackToHistory(it) {
                updateHistory()
            }
            openPlayer(it)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        historyAdapter = TrackAdapter(emptyList()) {
            viewModel.saveTrackToHistory(it) {
                updateHistory()
            }
            openPlayer(it)
        }
        findViewById<RecyclerView>(R.id.historyRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = historyAdapter
        }
    }

    private fun setupListeners() {
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                viewModel.loadHistory()
                updateHistory()
            } else {
                historyContainer.visibility = View.GONE
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                lastQuery = query

                clearButton.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                searchRunnable?.let { handler.removeCallbacks(it) }

                if (query.isEmpty() && searchEditText.hasFocus()) {
                    viewModel.tracks = emptyList()
                    viewModel.isError = false
                    viewModel.isEmptyResult = false
                    viewModel.isLoading = false

                    updateUI()
                    viewModel.loadHistory()
                    updateHistory()
                    return
                }

                searchRunnable = Runnable {
                    lifecycleScope.launch {
                        delay(2000)
                        viewModel.isLoading = true
                        updateUI()

                        viewModel.search(query)
                        updateUI()
                    }
                }
                handler.postDelayed(searchRunnable!!, 2000)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        clearButton.setOnClickListener {
            searchEditText.setText("")
            viewModel.loadHistory()
            updateHistory()
            updateUI()
            updateHistoryVisibility()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            updateHistory()
        }

        findViewById<TextView>(R.id.refresh_button).setOnClickListener {
            lifecycleScope.launch {
                viewModel.search(lastQuery)
                updateUI()
            }
        }
    }

    private fun updateUI() {
        progressBar.visibility = if (viewModel.isLoading) View.VISIBLE else View.GONE

        if (viewModel.isError) {
            errorPlaceholder.visibility = View.VISIBLE
            emptyPlaceholder.visibility = View.GONE
            recyclerView.visibility = View.GONE
            historyContainer.visibility = View.GONE
            return
        }

        if (viewModel.isEmptyResult) {
            emptyPlaceholder.visibility = View.VISIBLE
            errorPlaceholder.visibility = View.GONE
            recyclerView.visibility = View.GONE
            historyContainer.visibility = View.GONE
            return
        }

        adapter.updateTracks(viewModel.tracks)
        recyclerView.visibility = View.VISIBLE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        historyContainer.visibility = View.GONE
    }

    private fun updateHistory() {
        val history = viewModel.history
        historyAdapter.updateTracks(history)

        if (history.isNotEmpty()) {
            historyContainer.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            historyContainer.visibility = View.GONE
        }
    }

    private fun startPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("search_query", searchEditText.text.toString())
        super.onSaveInstanceState(outState)
    }

    private fun updateHistoryVisibility() {
        val history = viewModel.history
        val historyContainer = findViewById<LinearLayout>(R.id.historyContainer)
        val historyRecyclerView = findViewById<RecyclerView>(R.id.historyRecyclerView)
        val clearHistoryButton = findViewById<TextView>(R.id.clearHistoryButton)

        if (searchEditText.text.isEmpty() && searchEditText.hasFocus() && history.isNotEmpty()) {
            historyContainer.visibility = View.VISIBLE
            historyRecyclerView.layoutManager = LinearLayoutManager(this)
            historyRecyclerView.adapter = TrackAdapter(history) { track ->
                viewModel.saveToHistory(track)
                startPlayer(track)
            }
            clearHistoryButton.visibility = View.VISIBLE
        } else {
            historyContainer.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }
    }
}