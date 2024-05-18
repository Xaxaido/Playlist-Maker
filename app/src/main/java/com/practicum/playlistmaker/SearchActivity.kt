package com.practicum.playlistmaker.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.practicum.playlistmaker.databinding.ActivitySearchBinding

const val SEARCH_REQUEST = "SEARCH_REQUEST"

class SearchActivity : AppCompatActivity() {

    private var searchRequest = ""
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?.let { state ->
            searchRequest = state.getString(SEARCH_REQUEST, "")
            binding.searchLayout.searchText.setText(searchRequest)
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.searchLayout.buttonClear.setOnClickListener {
            binding.searchLayout.searchText.setText("")
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(binding.searchLayout.searchText.windowToken, 0)
        }
        binding.searchLayout.searchText.doOnTextChanged { text, _, _, _ ->
            searchRequest = text.toString()
            toggleVisibilityBtnClear(searchRequest)
        }
    }

    private fun toggleVisibilityBtnClear(text: String) {
        binding.searchLayout.buttonClear.visibility = if (text.isEmpty()) {
            View.GONE
        } else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(SEARCH_REQUEST, searchRequest)
    }
}