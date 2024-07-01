package com.practicum.playlistmaker.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.practicum.playlistmaker.data.PrefsMediator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.data.model.entity.Track
import com.practicum.playlistmaker.extension.network.TrackResponse
import com.practicum.playlistmaker.data.model.resources.VisibilityState
import com.practicum.playlistmaker.extension.util.RetrofitService
import com.practicum.playlistmaker.extension.util.Util
import com.practicum.playlistmaker.data.source.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private val prefs: PrefsMediator by lazy {
        PrefsMediator(applicationContext)
    }
    private val viewVisibilityList: VisibilityState.Views by lazy {
        VisibilityState.Views(
            mapOf(
                Pair(VisibilityState.ShowError, binding.networkFailure),
                Pair(VisibilityState.ShowNothingFound, binding.nothingFound),
                Pair(VisibilityState.ShowHistory, binding.searchHistory),
                Pair(VisibilityState.ShowContent, binding.recycler),
                Pair(VisibilityState.ShowProgress, binding.progressBar),
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RetrofitService.initialize(this)

        trackAdapter = TrackAdapter()
        binding.recycler.adapter = trackAdapter

        searchHistoryAdapter = TrackAdapter()
        binding.searchHistoryRecycler.adapter = searchHistoryAdapter

        setListeners()
        showHistory(prefs.getHistory())
    }

    @SuppressLint("SetTextI18n")
    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        trackAdapter.setOnClickListener { track ->
            prefs.addTrack(track)
            sendToPlayer(track)
        }

        searchHistoryAdapter.setOnClickListener { track ->
            prefs.addTrack(track)
            sendToPlayer(track)
        }

        binding.buttonRefresh.setOnClickListener { doSearch() }

        binding.searchLayout.buttonClear.setOnClickListener {
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(binding.searchLayout.searchText.windowToken, 0)

            binding.searchLayout.searchText.clearFocus()
            binding.searchLayout.searchText.setText("")
        }

        binding.btnClearHistory.setOnClickListener {
            prefs.clearHistory()
            binding.searchLayout.searchText.clearFocus()
            searchHistoryAdapter.submitTracksList(emptyList())
            showNoData()
        }

        binding.searchLayout.searchText.doOnTextChanged { text, _, _, _ ->
            val query = text.toString()

            if (query.isEmpty()) {
                showHistory(prefs.getHistory())
            } else showNoData()

            toggleVisibilityBtnClear(query)
        }

        binding.searchLayout.searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doSearch()
            }
            false
        }
    }

    private fun sendToPlayer(track: Track) = Intent(
        this,
        PlayerActivity::class.java
    ).apply {
        putExtra(Util.KEY_TRACK, track)
        startActivity(this)
    }

    private fun toggleVisibilityBtnClear(text: String) {
        binding.searchLayout.buttonClear.isVisible = text.isNotEmpty()
    }

    private fun showLoading() {
        viewVisibilityList.updateVisibility(VisibilityState.ShowProgress)
    }

    private fun showNothingFound() {
        viewVisibilityList.updateVisibility(VisibilityState.ShowNothingFound)
    }

    private fun showError() {
        viewVisibilityList.updateVisibility(VisibilityState.ShowError)
    }

    private fun showNoData() {
        viewVisibilityList.updateVisibility(VisibilityState.ShowNoData)
    }

    private fun showHistory(list: List<Track>) {
        list.apply {
            if (this.isNotEmpty()) {
                updateData(searchHistoryAdapter, list) {
                    viewVisibilityList.updateVisibility(VisibilityState.ShowHistory)
                }
            } else showNoData()
        }
    }

    private fun showContent(list: List<Track>) {
        updateData(trackAdapter, list) {
            viewVisibilityList.updateVisibility(VisibilityState.ShowContent)
        }
    }

    private fun updateData(adapter: TrackAdapter, list: List<Track>, onFinish: () -> Unit) {
        adapter.submitTracksList(list, onFinish)
    }

    private fun doSearch() {
        val query = binding.searchLayout.searchText.text.toString()
        if (query.isNotEmpty()) searchTracks(query)
    }

    private fun searchTracks(term: String) {
        showLoading()
        RetrofitService.iTunes?.search(term)?.enqueue(object : Callback<TrackResponse> {

            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                val tracks = response.body()?.results
                if (tracks.isNullOrEmpty()) {
                    showNothingFound()
                } else {
                    showContent(tracks)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showError()
            }
        })
    }
}