package com.practicum.playlistmaker.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.resources.TracksSearchState
import com.practicum.playlistmaker.data.resources.VisibilityState.Error
import com.practicum.playlistmaker.data.resources.VisibilityState.History
import com.practicum.playlistmaker.data.resources.VisibilityState.Loading
import com.practicum.playlistmaker.data.resources.VisibilityState.NoData
import com.practicum.playlistmaker.data.resources.VisibilityState.NothingFound
import com.practicum.playlistmaker.data.resources.VisibilityState.SearchResults
import com.practicum.playlistmaker.data.resources.VisibilityState.ViewsList
import com.practicum.playlistmaker.data.resources.VisibilityState.VisibilityItem
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.api.TracksMediator
import com.practicum.playlistmaker.extension.util.Debounce
import com.practicum.playlistmaker.extension.util.Util
import com.practicum.playlistmaker.extension.widget.recyclerView.StickyFooterDecoration
import com.practicum.playlistmaker.presentation.player.PlayerActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var stickyFooterDecoration: StickyFooterDecoration
    private var searchRequest = ""
    private var isHistoryShowed = true
    private var isClickEnabled = true
    private var isKeyboardVisible = false
    private val timer = Debounce(delay = Util.USER_INPUT_DELAY) { searchTracks() }
    private val prefs: SearchHistoryRepositoryImpl by lazy {
        Creator.getSearchHistoryRepositoryImpl(this)
    }
    private val alisa: ViewsList by lazy {
        ViewsList(
            listOf(
                VisibilityItem(binding.networkFailure, listOf(Error)),
                VisibilityItem(binding.nothingFound, listOf(NothingFound)),
                VisibilityItem(binding.progressBar, listOf(Loading)),
                VisibilityItem(binding.stickyContainer, listOf(History)),
                VisibilityItem(binding.blurImageViewFooter, listOf(History)),
                VisibilityItem(binding.recycler, listOf(History, SearchResults)),
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackAdapter = TrackAdapter()
        binding.recycler.adapter = trackAdapter
        binding.recycler.itemAnimator = null
        stickyFooterDecoration = StickyFooterDecoration()
        setListeners()
        showNoData()
    }

    private fun updateKeyboardVisibility() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.contentLayout.windowInsetsController?.addOnControllableInsetsChangedListener { _, insets ->
                isKeyboardVisible = (insets and WindowInsetsCompat.Type.ime()) != 0
            }
        } else {
            binding.contentLayout.viewTreeObserver.addOnGlobalLayoutListener {
                val heightDiff = binding.contentLayout.rootView.height - binding.contentLayout.height
                isKeyboardVisible = heightDiff > 200
            }
        }
    }

    private fun setListeners() {
        updateKeyboardVisibility()
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && isKeyboardVisible) hideKeyboard()
            }
        })

        trackAdapter.setOnTrackClickListener { track ->
            if (!isClickEnabled) return@setOnTrackClickListener

            prefs.addTrack(track)
            sendToPlayer(track)
            isClickEnabled = false
            Debounce(delay = Util.BUTTON_ENABLED_DELAY) { isClickEnabled = true }.start()
        }

        trackAdapter.setOnClearHistoryClick {
            prefs.clearHistory()
            binding.searchLayout.searchText.clearFocus()
            showNoData()
        }

        binding.buttonRefresh.setOnClickListener { searchTracks() }

        binding.searchLayout.buttonClear.setOnClickListener {
            hideKeyboard()
            isHistoryShowed = true
            binding.searchLayout.searchText.clearFocus()
            binding.searchLayout.searchText.text = Editable.Factory.getInstance().newEditable("")
        }

        binding.searchLayout.searchText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchRequest.isEmpty()) showHistory(prefs.getHistory())
            else showNoData()
        }

        binding.searchLayout.searchText.doOnTextChanged { text, _, _, _ ->
            searchRequest = text.toString()
            binding.searchLayout.buttonClear.isVisible = searchRequest.isNotEmpty()

            if (searchRequest.isEmpty() && binding.searchLayout.searchText.hasFocus()) {
                showHistory(prefs.getHistory())
            } else if (isHistoryShowed) showNoData()

            timer.start()
        }
    }

    private fun hideKeyboard() {
        isKeyboardVisible = false
        val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(binding.searchLayout.searchText.windowToken, 0)
    }

    private fun sendToPlayer(track: Track) = Intent(
        this,
        PlayerActivity::class.java,
    ).apply {
        putExtra(Util.KEY_TRACK, track)
        startActivity(this)
    }

    private fun showNoData() {
        updateData(false, emptyList()) {
            alisa show NoData
        }
    }

    private fun showHistory(list: List<Track>) {
        isHistoryShowed = true
        list.apply {
            if (this.isNotEmpty()) {
                updateData(true, this) {
                    alisa show History
                }
            } else showNoData()
        }
    }

    private fun showContent(list: List<Track>) {
        isHistoryShowed = false
        updateData(false, list) {
            alisa show SearchResults
        }
    }

    private fun updateData(isDecorationNeeded: Boolean, list: List<Track>, onFinish: () -> Unit) {
        if (!isDecorationNeeded) stickyFooterDecoration.detach()
        else stickyFooterDecoration.attachRecyclerView(binding.recycler, trackAdapter)

        trackAdapter.submitTracksList(isDecorationNeeded, list, onFinish)
    }

    private fun searchTracks() {
        if (searchRequest.isEmpty()) return

        alisa show Loading
        Creator.provideTracksMediator(this).searchTracks(searchRequest, object : TracksMediator.TracksConsumer {

            override fun consume(result: TracksSearchState) {
                when(result.tracks.isEmpty()) {
                    false -> showContent(result.tracks)
                    else -> {
                        Debounce(delay = 0L) {
                            if (result.error != null) alisa show Error
                            else alisa show NothingFound
                        }.start()
                    }
                }
            }
        })
    }
}