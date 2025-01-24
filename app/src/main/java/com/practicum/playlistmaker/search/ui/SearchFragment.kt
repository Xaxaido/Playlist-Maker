package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.SearchState
import com.practicum.playlistmaker.common.resources.VisibilityState.Error
import com.practicum.playlistmaker.common.resources.VisibilityState.History
import com.practicum.playlistmaker.common.resources.VisibilityState.Loading
import com.practicum.playlistmaker.common.resources.VisibilityState.NoData
import com.practicum.playlistmaker.common.resources.VisibilityState.NothingFound
import com.practicum.playlistmaker.common.resources.VisibilityState.Results
import com.practicum.playlistmaker.common.resources.VisibilityState.ViewsList
import com.practicum.playlistmaker.common.resources.VisibilityState.VisibilityItem
import com.practicum.playlistmaker.common.utils.Debounce
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.common.widgets.recycler.ItemAnimator
import com.practicum.playlistmaker.common.widgets.recycler.PaddingItemDecoration
import com.practicum.playlistmaker.common.widgets.recycler.SwipeHelper
import com.practicum.playlistmaker.common.widgets.recycler.UnderlayButton
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.main.domain.api.BackButtonState
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.recycler.TrackAdapter
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var swipeHelper: SwipeHelper
    private var searchRequest = ""
    private var isClickEnabled = true
    private var isKeyboardVisible = false
    private lateinit var visibility: ViewsList
    private val keyboardStateListener = ViewTreeObserver.OnGlobalLayoutListener {
        view?.also { screen ->
            val r = Rect().apply { screen.getWindowVisibleDisplayFrame(this) }
            val keyboardHeight = screen.height - r.bottom
            isKeyboardVisible = keyboardHeight > screen.height * 0.1
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        (activity as? BackButtonState)?.updateBackBtn(false)
    }

    override fun onStop() {
        super.onStop()
        view?.viewTreeObserver?.removeOnGlobalLayoutListener(keyboardStateListener)
    }

    private fun setupUI() {
        visibility = ViewsList(
            listOf(
                VisibilityItem(binding.networkFailure, listOf(Error)),
                VisibilityItem(binding.nothingFound, listOf(NothingFound)),
                VisibilityItem(binding.progressBar, listOf(Loading)),
                VisibilityItem(binding.stickyContainer.clearHistory, listOf(History)),
                VisibilityItem(binding.recycler, listOf(History, Results)),
            )
        )

        trackAdapter = TrackAdapter(
            { track ->
                if (!isClickEnabled) return@TrackAdapter

                isClickEnabled = false
                viewModel.addToHistory(track)
                sendToPlayer(Util.trackToJson(track))
                Debounce<Any>(Util.BUTTON_ENABLED_DELAY, lifecycleScope) { isClickEnabled = true }.start()
            },
            onClearHistoryClick = { clearHistory() }
        )

        binding.recycler.adapter = trackAdapter
        binding.recycler.itemAnimator = ItemAnimator()
        swipeHelper = initSwipeHelper()
        binding.recycler.addItemDecoration(PaddingItemDecoration(
            intArrayOf(
                resources.getDimensionPixelSize(R.dimen.search_actionbar_height),
                resources.getDimensionPixelSize(R.dimen.toolbar_height),
            )
        ))

        binding.blurImageViewActionbar.setContentView(binding.recycler)
        binding.blurImageViewBottomMenu.setContentView(binding.recycler)
    }

    private fun initSwipeHelper() = object : SwipeHelper(binding.recycler) {

        override fun instantiateUnderlayButton(pos: Int) =
            if (viewModel.isHistoryVisible == true) {
                mutableListOf(btnDelete(), btnAddToFav(pos))
            } else {
                mutableListOf(btnAddToFav(pos))
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        isKeyboardVisible()
        binding.buttonRefresh.setOnClickListener { searchTracks() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { state ->
                    renderState(state)
                }
            }
        }

        binding.recycler.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                if (isKeyboardVisible) hideKeyboard()
            }
            false
        }

        binding.stickyContainer.btnClearHistory.setOnClickListener { clearHistory() }

        binding.searchLayout.buttonClear.setOnClickListener {
            hideKeyboard()
            viewModel.isHistoryVisible = true
            binding.searchLayout.searchText.setText("")
            viewModel.getHistory(true)
        }

        binding.searchLayout.searchText.also { editText ->

            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && searchRequest.isEmpty() && viewModel.isHistoryVisible != true) viewModel.getHistory(true)
            }

            editText.doOnTextChanged { text, _, _, _ ->
                val hasFocus = editText.hasFocus()

                searchRequest = text.toString()
                updateClearBtnVisibility(searchRequest.isNotEmpty())

                if (hasFocus) searchTracks()
                if (hasFocus && searchRequest.isEmpty()) {
                    viewModel.getHistory(true)
                }
            }
        }
    }

    private fun clearHistory() {
        viewModel.clearHistory()
        binding.searchLayout.searchText.clearFocus()
        showNoData()
    }

    private fun updateClearHistoryBtnPosition() {
        val layoutManager = binding.recycler.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
        val isButtonVisible = trackAdapter.itemCount - 1 > lastVisibleItemPosition || firstVisibleItemPosition > 0
        trackAdapter.updateFooterVisibility(!isButtonVisible)
        binding.stickyContainer.clearHistory.isVisible = isButtonVisible
    }

    private fun updateClearBtnVisibility(isVisible: Boolean) {
        binding.searchLayout.buttonClear.isVisible = isVisible
    }

    private fun isKeyboardVisible() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view?.windowInsetsController?.addOnControllableInsetsChangedListener { _, insets ->
                isKeyboardVisible = (insets and WindowInsetsCompat.Type.ime()) != 0
            }
        } else {
            view?.viewTreeObserver?.addOnGlobalLayoutListener(keyboardStateListener)
        }
    }

    private fun hideKeyboard() {
        isKeyboardVisible = false
        val keyboard = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(binding.searchLayout.searchText.windowToken, 0)
    }

    private fun sendToPlayer(json: String) {
        findNavController().navigate(
            R.id.action_send_to_player,
            PlayerFragment.createArgs(json),
        )
    }

    private fun searchTracks() {viewModel.search(searchRequest) }

    private fun showNoData() {
        trackAdapter.submitTracksList(false, emptyList()) {
            visibility.show(NoData)
        }
    }

    private fun showError(error: Int) {
        when (error) {
            Util.REQUEST_CANCELLED -> viewModel.getHistory(true)
            else -> visibility.show(Error)
        }
    }

    private fun showSearchHistory(list: List<Track>, isDataSetChanged: Boolean = true) {
        viewModel.isHistoryVisible = true
        if (list.isNotEmpty()) {
            visibility.show(History, visibility.items.filter { it.view.id != binding.stickyContainer.clearHistory.id })
            trackAdapter.submitTracksList(true, list, isDataSetChanged) {
                binding.recycler.post {
                    updateClearHistoryBtnPosition()
                    binding.recycler.invalidateItemDecorations()
                }
            }
        } else {
            showNoData()
        }
    }

    private fun showSearchResults(list: List<Track>, isDataSetChanged: Boolean = true) {
        viewModel.isHistoryVisible = false
        trackAdapter.submitTracksList(false, list, isDataSetChanged)  {
            visibility.show(Results)
        }
    }

    private fun renderState(state: SearchState) {
        if (state.term != null && searchRequest != state.term) return

        when (state) {
            is SearchState.NoData -> showNoData()
            is SearchState.Loading -> visibility.show(Loading)
            is SearchState.TrackSearchResults -> showSearchResults(state.results, state.isDataSetChanged)
            is SearchState.TrackSearchHistory -> showSearchHistory(state.history, state.isDataSetChanged)
            is SearchState.ConnectionError -> showError(state.error)
            is SearchState.NothingFound -> visibility.show(NothingFound)
        }
    }

    private val btnDelete: () -> UnderlayButton = {
        UnderlayButton(
            requireActivity(),
            getString(R.string.history_delete_item),
            R.drawable.ic_delete,
            bgColor = requireActivity().getColor(R.color.red),
            textColor = requireActivity().getColor(R.color.white),
        ) { pos ->
            swipeHelper.disableClick()
            trackAdapter.notifyItemChanged(pos)
            viewModel.removeFromHistory(pos - 1)
            Debounce<Any>(Util.ANIMATION_SHORT, viewLifecycleOwner.lifecycleScope) {
                swipeHelper.startParticleAnimation(binding.particleView, pos) {
                    viewModel.getHistory(false)
                    swipeHelper.enableClick()
                }
            }.start()
        }
    }

    private val btnAddToFav: (Int) -> UnderlayButton = {
        val track = trackAdapter.getItem(it)!!
        val icon = if (track.isFavorite) R.drawable.favorite else R.drawable.ic_add_to_fav
        UnderlayButton(
            requireActivity(),
            getString(R.string.history_add_to_fav),
            icon,
            bgColor = requireActivity().getColor(R.color.greyLight),
            textColor = requireActivity().getColor(R.color.black),
        ) { pos ->
            viewModel.addToFavorites(track)
            trackAdapter.notifyItemChanged(pos)
        }
    }
}