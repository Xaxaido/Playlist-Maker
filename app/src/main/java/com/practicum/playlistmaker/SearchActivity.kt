package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.extension.network.TrackResponse
import com.practicum.playlistmaker.extension.state.VisibilityState
import com.practicum.playlistmaker.extension.util.RetrofitService
import com.practicum.playlistmaker.source.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private val viewVisibilityList: VisibilityState.Views by lazy {
        VisibilityState.Views(
            mapOf(
                Pair(VisibilityState.ShowError, binding.networkFailure),
                Pair(VisibilityState.ShowNothingFound, binding.nothingFound),
                Pair(VisibilityState.ShowContent, binding.recycler),
                Pair(VisibilityState.ShowProgress, binding.progressBar)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackAdapter = TrackAdapter()
        binding.recycler.adapter = trackAdapter
        setListeners()
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.buttonRefresh.setOnClickListener { doSearch() }

        binding.searchLayout.buttonClear.setOnClickListener {
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(binding.searchLayout.searchText.windowToken, 0)

            binding.searchLayout.searchText.setText("")
            trackAdapter.submitTracksList(emptyList())
        }

        binding.searchLayout.searchText.doOnTextChanged { text, _, _, _ ->
            toggleVisibilityBtnClear(text.toString())
        }

        binding.searchLayout.searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doSearch()
            }
            false
        }
    }

    private fun toggleVisibilityBtnClear(text: String) {
        binding.searchLayout.buttonClear.isVisible = text.isNotEmpty()
    }

    private fun doSearch() {
        val query = binding.searchLayout.searchText.text.toString()
        if (query.isNotEmpty()) searchTracks(query)
    }

    private fun searchTracks(term: String) {
        trackAdapter.submitTracksList(emptyList())
        viewVisibilityList.updateVisibility(VisibilityState.ShowProgress)

        RetrofitService.iTunes.search(term).enqueue(object : Callback<TrackResponse> {

            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                val tracks = response.body()?.results
                if (tracks.isNullOrEmpty()) {
                    viewVisibilityList.updateVisibility(VisibilityState.ShowNothingFound)
                } else {
                    trackAdapter.submitTracksList(tracks) {
                        viewVisibilityList.updateVisibility(VisibilityState.ShowContent)
                    }
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                viewVisibilityList.updateVisibility(VisibilityState.ShowError)
            }

        })
    }
}