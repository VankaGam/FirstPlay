package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private var tracks: List<Track> = emptyList()
    private val trackRepository = TrackRepository()
    private var lastSearchTerm: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        initViews()
        setListeners()
        setupWindowInsets()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(tracks)
        recyclerView.adapter = adapter

        if (savedInstanceState != null) {
            val savedQuery = savedInstanceState.getString("search_query")
            searchEditText.setText(savedQuery)
            if (!savedQuery.isNullOrEmpty()) {
                performSearch(savedQuery)
            }
        }
    }

    private fun initViews() {
        backButton = findViewById(R.id.back)
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
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
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateClearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        findViewById<TextView>(R.id.refresh_button).setOnClickListener {
            lastSearchTerm?.let { term ->
                performSearch(term)
            }
        }
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
        adapter = TrackAdapter(tracks)
        recyclerView.adapter = adapter
        findViewById<LinearLayout>(R.id.emptyPlaceholder).visibility = View.GONE
        findViewById<LinearLayout>(R.id.errorPlaceholder).visibility = View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(InputMethodManager::class.java)
        inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun performSearch(term: String) {
        lastSearchTerm = term
        val apiService = RetrofitInstance.api
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
                                track.artworkUrl100
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
            }
        })
    }

    private fun showResults(tracks: List<Track>) {
        findViewById<RecyclerView>(R.id.recyclerView).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.emptyPlaceholder).visibility = View.GONE
        findViewById<LinearLayout>(R.id.errorPlaceholder).visibility = View.GONE

        adapter = TrackAdapter(tracks)
        recyclerView.adapter = adapter
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
}
