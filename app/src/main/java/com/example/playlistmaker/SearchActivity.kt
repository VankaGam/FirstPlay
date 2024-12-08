package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        initViews()
        setListeners()
        setupWindowInsets()
    }

    private fun initViews() {
        backButton = findViewById(R.id.back)
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setListeners() {
        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            clearSearchQuery()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateClearButtonVisibility(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {}
        })
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
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(InputMethodManager::class.java)
        inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}
