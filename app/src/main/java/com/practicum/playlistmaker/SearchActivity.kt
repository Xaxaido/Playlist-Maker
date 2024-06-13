package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.practicum.playlistmaker.data.entity.Track
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.source.TrackAdapter
import com.practicum.playlistmaker.extension.network.TrackResponse
import com.practicum.playlistmaker.extension.util.Debounce
import com.practicum.playlistmaker.extension.util.RetrofitService
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private val timer: Debounce by lazy {
        Debounce(dispatcher = Dispatchers.Main) { doSearch() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()

        trackAdapter = TrackAdapter()
        binding.recycler.adapter = trackAdapter
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
            val request = text.toString()
            toggleVisibilityBtnClear(request)
            if (request.isNotEmpty()) timer.start(false)
        }
    }

    private fun toggleVisibilityBtnClear(text: String) {
        binding.searchLayout.buttonClear.isVisible = text.isNotEmpty()
    }

    private fun showError() {
        binding.networkFailure.isVisible = true
        binding.nothingFound.isVisible = false
    }

    private fun showNothingFound() {
        binding.networkFailure.isVisible = false
        binding.nothingFound.isVisible = true
    }

    private fun showContent(list: List<Track>) {
        binding.recycler.isVisible = true
        updateData(list)
    }

    private fun updateData(list: List<Track>) {
        binding.alerts.isVisible = false
        trackAdapter.submitTracksList(list)
    }

    private fun doSearch() {
        searchTracks(binding.searchLayout.searchText.text.toString())
    }

    private fun searchTracks(term: String) {
        binding.progressBar.isVisible = true

        RetrofitService.iTunes.search(term).enqueue(object : Callback<TrackResponse> {

            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                response.body()?.results?.let { tracks ->
                    if (tracks.isEmpty()) showNothingFound()
                    else showContent(tracks)
                } ?: showNothingFound()
                binding.progressBar.isVisible = false
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showError()
                binding.progressBar.isVisible = false
            }

        })
    }
}