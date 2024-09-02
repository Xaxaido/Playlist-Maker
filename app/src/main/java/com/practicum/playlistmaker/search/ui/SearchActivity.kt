package com.practicum.playlistmaker.search.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.common.resources.SearchState
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.utils.DtoConverter.toTrackParcelable
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.common.resources.VisibilityState.Error
import com.practicum.playlistmaker.common.resources.VisibilityState.History
import com.practicum.playlistmaker.common.resources.VisibilityState.Loading
import com.practicum.playlistmaker.common.resources.VisibilityState.NoData
import com.practicum.playlistmaker.common.resources.VisibilityState.NothingFound
import com.practicum.playlistmaker.common.resources.VisibilityState.SearchResults
import com.practicum.playlistmaker.common.resources.VisibilityState.ViewsList
import com.practicum.playlistmaker.common.resources.VisibilityState.VisibilityItem
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.widgets.recycler.StickyFooterDecoration
import com.practicum.playlistmaker.common.widgets.recycler.TrackItemAnimator
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels {
        SearchViewModel.getViewModelFactory()
    }
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var stickyFooterDecoration: StickyFooterDecoration
    private var searchRequest = ""
    private var isHistoryShowed = true
    private var isClickEnabled = true
    private var isKeyboardVisible = false
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
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && isHistoryShowed) {
                showHistory(viewModel.getHistory())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackAdapter = TrackAdapter()
        binding.recycler.adapter = trackAdapter
        binding.recycler.itemAnimator = TrackItemAnimator()
        stickyFooterDecoration = StickyFooterDecoration()
        setListeners()
        showNoData()
    }

    private fun isKeyboardVisible() {
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
        isKeyboardVisible()
        binding.toolbar.setNavigationOnClickListener { finish() }
        viewModel.liveData.observe(this, ::renderState)
        binding.buttonRefresh.setOnClickListener { searchTracks() }

        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && isKeyboardVisible) hideKeyboard()
            }
        })

        trackAdapter.setOnTrackClickListener { track ->
            if (!isClickEnabled) return@setOnTrackClickListener

            isClickEnabled = false
            viewModel.addToHistory(track)
            sendToPlayer(track)
            Debounce(delay = Util.BUTTON_ENABLED_DELAY) { isClickEnabled = true }.start()
        }

        trackAdapter.setOnClearHistoryClick {
            viewModel.clearHistory()
            binding.searchLayout.searchText.clearFocus()
            showNoData()
        }

        binding.searchLayout.buttonClear.setOnClickListener {
            hideKeyboard()
            isHistoryShowed = true
            binding.searchLayout.searchText.clearFocus()
            binding.searchLayout.searchText.text = Editable.Factory.getInstance().newEditable("")
        }

        binding.searchLayout.searchText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchRequest.isEmpty()) showHistory(viewModel.getHistory())
            else showNoData()
        }

        binding.searchLayout.searchText.doOnTextChanged { text, _, _, _ ->
            searchRequest = text.toString()
            binding.searchLayout.buttonClear.isVisible = searchRequest.isNotEmpty()

            if (searchRequest.isEmpty() && binding.searchLayout.searchText.hasFocus()) {
                showHistory(viewModel.getHistory())
            } else if (isHistoryShowed) showNoData()

            searchTracks()
        }
    }

    private fun hideKeyboard() {
        isKeyboardVisible = false
        val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(binding.searchLayout.searchText.windowToken, 0)
    }

    private fun searchTracks() {
        viewModel.search(searchRequest)
    }

    private fun sendToPlayer(track: Track) = Intent(
        this,
        PlayerActivity::class.java,
    ).apply {
        putExtra(Util.KEY_TRACK, track.toTrackParcelable())
        startForResult.launch(this@apply)
    }

    private fun showNoData() {
        updateData(false, emptyList()) {
            alisa show NoData
        }
    }

    private fun showHistory(list: List<Track>) {
        isHistoryShowed = true
        list.also {
            if (it.isNotEmpty()) {
                updateData(true, it) {
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

    private fun updateData(isDecorationNeeded: Boolean, list: List<Track>, doOnEnd: () -> Unit) {
        if (!isDecorationNeeded) stickyFooterDecoration.detach()
        else stickyFooterDecoration.attachRecyclerView(binding.recycler, trackAdapter)

        trackAdapter.submitTracksList(isDecorationNeeded, list, doOnEnd)
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.InternetResults -> showContent(state.results)
            is SearchState.ConnectionError -> alisa show Error
            is SearchState.InternetHistory -> showHistory(state.history)
            is SearchState.NothingFound -> alisa show NothingFound
            is SearchState.Loading -> alisa show Loading
        }
    }
}