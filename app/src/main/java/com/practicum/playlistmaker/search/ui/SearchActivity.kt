package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.SearchState
import com.practicum.playlistmaker.common.resources.VisibilityState.Error
import com.practicum.playlistmaker.common.resources.VisibilityState.History
import com.practicum.playlistmaker.common.resources.VisibilityState.Loading
import com.practicum.playlistmaker.common.resources.VisibilityState.NoData
import com.practicum.playlistmaker.common.resources.VisibilityState.NothingFound
import com.practicum.playlistmaker.common.resources.VisibilityState.SearchResults
import com.practicum.playlistmaker.common.resources.VisibilityState.ViewsList
import com.practicum.playlistmaker.common.resources.VisibilityState.VisibilityItem
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.widgets.recycler.ItemAnimator
import com.practicum.playlistmaker.common.widgets.recycler.ParticleAnimator
import com.practicum.playlistmaker.common.widgets.recycler.StickyFooterDecoration
import com.practicum.playlistmaker.common.widgets.recycler.SwipeHelper
import com.practicum.playlistmaker.common.widgets.recycler.UnderlayButton
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.recycler.TrackAdapter
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var stickyFooterDecoration: StickyFooterDecoration
    private lateinit var swipeHelper: SwipeHelper
    private var searchRequest = ""
    private var isHistoryShown = false
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setListeners()
        showNoData()
    }

    override fun onResume() {
        super.onResume()
        if (isHistoryShown) viewModel.getHistory(false)
    }

    private fun setupUI() {
        trackAdapter = TrackAdapter()
        binding.recycler.adapter = trackAdapter
        binding.recycler.itemAnimator = ItemAnimator()
        stickyFooterDecoration = StickyFooterDecoration()
        swipeHelper = initSwipeHelper()
    }

    private fun initSwipeHelper() = object : SwipeHelper(this, binding.recycler) {

        override fun instantiateUnderlayButton() =
            if (isHistoryShown) {
                mutableListOf(btnDelete(), btnAddToFav())
            } else {
                mutableListOf(btnAddToFav())
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
            viewModel.sendToPlayer(track)
            Debounce(delay = Util.BUTTON_ENABLED_DELAY) { isClickEnabled = true }.start()
        }

        trackAdapter.setOnClearHistoryClick {
            viewModel.clearHistory()
            binding.searchLayout.searchText.clearFocus()
            showNoData()
        }

        binding.searchLayout.buttonClear.setOnClickListener {
            hideKeyboard()
            isHistoryShown = true
            binding.searchLayout.searchText.setText("")
        }

        binding.searchLayout.searchText.setOnFocusChangeListener { _, hasFocus ->
            updateSearchHistoryVisibility(hasFocus)
        }

        binding.searchLayout.searchText.doOnTextChanged { text, _, _, _ ->
            searchRequest = text.toString()
            updateClearBtnVisibility(searchRequest.isNotEmpty())
            updateSearchHistoryVisibility(binding.searchLayout.searchText.hasFocus())

            if (searchRequest.isNotEmpty()) searchTracks()
            else viewModel.stopSearch()
        }
    }

    private fun updateSearchHistoryVisibility(hasFocus: Boolean) {
        if (hasFocus && searchRequest.isEmpty()) viewModel.getHistory(true)
        else if (isHistoryShown) showNoData()
    }

    private fun updateClearBtnVisibility(isVisible: Boolean) {
        binding.searchLayout.buttonClear.isVisible = isVisible
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

    private fun hideKeyboard() {
        isKeyboardVisible = false
        val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(binding.searchLayout.searchText.windowToken, 0)
    }

    private fun sendToPlayer(json: String) {
        Intent(
            this,
            PlayerActivity::class.java,
        ).apply {
            putExtra(Util.KEY_TRACK, json)
            startActivity(this)
        }
    }

    private fun searchTracks() { viewModel.search(searchRequest) }

    private fun showNoData() {
        updateData(false, emptyList()) {
            alisa show NoData
        }
    }

    private fun showError(error: Int) {
        when (error) {
            Util.REQUEST_CANCELLED -> viewModel.getHistory(false)
            else -> alisa show Error
        }
    }

    private fun showHistory(list: List<Track>, isDataSetChanged: Boolean = true) {
        isHistoryShown = true
        list.also {
            if (it.isNotEmpty()) {
                updateData(true, it, isDataSetChanged) {
                    alisa show History
                }
            } else showNoData()
        }
    }

    private fun showContent(list: List<Track>) {
        isHistoryShown = false
        updateData(false, list) {
            alisa show SearchResults
        }
    }

    private fun updateData(
        isDecorationNeeded: Boolean,
        list: List<Track>,
        isDataSetChanged: Boolean = true,
        doOnEnd: () -> Unit
    ) {
        if (!isDecorationNeeded) stickyFooterDecoration.detach()
        else stickyFooterDecoration.attachRecyclerView(binding.recycler, trackAdapter)

        trackAdapter.submitTracksList(isDecorationNeeded, list, isDataSetChanged, doOnEnd)
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.SendTrackToPlayer -> sendToPlayer(state.json)
            is SearchState.TrackSearchResults -> showContent(state.results)
            is SearchState.TrackSearchHistory -> showHistory(state.history, state.isDataSetChanged)
            is SearchState.ConnectionError -> showError(state.error)
            is SearchState.NothingFound -> alisa show NothingFound
            is SearchState.Loading -> alisa show Loading
        }
    }

    private fun startParticleAnimation(pos: Int, onAnimationEnd: () -> Unit) {
        val viewToRemove = binding.recycler.findViewHolderForAdapterPosition(pos)?.itemView ?: return
        val bitmap = Bitmap.createBitmap(viewToRemove.width, viewToRemove.height, Bitmap.Config.ARGB_8888)

        Canvas(bitmap).also { viewToRemove.draw(it) }
        viewToRemove.isVisible = false
        binding.particleView.animator = ParticleAnimator(
            this,
            binding.particleView,
            bitmap,
            0f,
            viewToRemove.top.toFloat()
        )
        binding.particleView.startAnimation { onAnimationEnd() }
    }

    private val btnDelete: () -> UnderlayButton = {
        UnderlayButton(
            this,
            getString(R.string.history_delete_item),
            R.drawable.ic_delete,
            getColor(R.color.red),
            getColor(R.color.white)
        ) { pos ->
            trackAdapter.notifyItemChanged(pos)
            viewModel.removeFromHistory(pos - 1)
            Debounce(Util.ANIMATION_SHORT) {
                startParticleAnimation(pos) {
                    viewModel.getHistory(false)
                }
            }.start()
        }
    }

    private val btnAddToFav: () -> UnderlayButton = {
        UnderlayButton(
            this,
            getString(R.string.history_add_to_fav),
            R.drawable.ic_add_to_fav,
            getColor(R.color.greyLight),
            getColor(R.color.black)
        ) { pos ->
            trackAdapter.notifyItemChanged(pos)
        }
    }
}