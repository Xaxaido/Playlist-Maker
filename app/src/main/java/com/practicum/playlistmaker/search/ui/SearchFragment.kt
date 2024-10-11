package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
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
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.common.widgets.recycler.ItemAnimator
import com.practicum.playlistmaker.common.widgets.recycler.PaddingItemDecoration
import com.practicum.playlistmaker.common.widgets.recycler.ParticleAnimator
import com.practicum.playlistmaker.common.widgets.recycler.SwipeHelper
import com.practicum.playlistmaker.common.widgets.recycler.UnderlayButton
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.main.domain.api.BackButtonState
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.recycler.TrackAdapter
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var swipeHelper: SwipeHelper
    private var searchRequest = ""
    private var isHistoryVisible = false
    private var isClickEnabled = true
    private var isKeyboardVisible = false
    private lateinit var alisa: ViewsList
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
        alisa = ViewsList(
            listOf(
                VisibilityItem(binding.networkFailure, listOf(Error)),
                VisibilityItem(binding.nothingFound, listOf(NothingFound)),
                VisibilityItem(binding.progressBar, listOf(Loading)),
                VisibilityItem(binding.stickyContainer.clearHistory, listOf(History)),
                VisibilityItem(binding.recycler, listOf(History, SearchResults)),
            )
        )
        trackAdapter = TrackAdapter(binding.recycler)
        binding.recycler.adapter = trackAdapter
        binding.recycler.itemAnimator = ItemAnimator()
        swipeHelper = initSwipeHelper()
        binding.recycler.addItemDecoration(PaddingItemDecoration(
            intArrayOf(
                resources.getDimensionPixelSize(R.dimen.search_actionbar_height),
                resources.getDimensionPixelSize(R.dimen.toolbar_height),
            )
        ))
    }

    private fun initSwipeHelper() = object : SwipeHelper(requireActivity(), binding.recycler) {

        override fun instantiateUnderlayButton() =
            if (isHistoryVisible) {
                mutableListOf(btnDelete(), btnAddToFav())
            } else {
                mutableListOf(btnAddToFav())
            }
    }

    private fun setListeners() {
        isKeyboardVisible()
        viewModel.liveData.observe(viewLifecycleOwner, ::renderState)
        binding.buttonRefresh.setOnClickListener { searchTracks() }

        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isKeyboardVisible) hideKeyboard()
            }
        })

        trackAdapter.setOnTrackClickListener { track ->
            if (!isClickEnabled) return@setOnTrackClickListener

            isClickEnabled = false
            viewModel.addToHistory(track)
            sendToPlayer(viewModel.trackToJson(track))
            Debounce(delay = Util.BUTTON_ENABLED_DELAY) { isClickEnabled = true }.start()
        }

        trackAdapter.setOnClearHistoryClick {
            viewModel.clearHistory()
            binding.searchLayout.searchText.clearFocus()
            showNoData()
        }

        binding.searchLayout.buttonClear.setOnClickListener {
            hideKeyboard()
            isHistoryVisible = true
            binding.searchLayout.searchText.setText("")
            viewModel.getHistory(true)
        }

        binding.searchLayout.searchText.also { editText ->

            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && searchRequest.isEmpty() && !isHistoryVisible) viewModel.getHistory(true)
            }

            editText.doOnTextChanged { text, _, _, _ ->
                val hasFocus = editText.hasFocus()

                searchRequest = text.toString()
                updateClearBtnVisibility(searchRequest.isNotEmpty())

                if (hasFocus) searchTracks()
                if (hasFocus && searchRequest.isEmpty()) {
                    viewModel.stopSearch()
                    viewModel.getHistory(true)
                }
            }
        }
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
            alisa show NoData
        }
    }

    private fun showError(error: Int) {
        alisa show Error
    }

    private fun showSearchHistory(list: List<Track>, isDataSetChanged: Boolean = true) {
        isHistoryVisible = true
        if (list.isNotEmpty()) {
            trackAdapter.submitTracksList(true, list, isDataSetChanged) { isButtonVisible ->
                alisa show History
                binding.stickyContainer.clearHistory.isVisible = isButtonVisible
                binding.recycler.post { binding.recycler.invalidateItemDecorations() }
            }
        } else {
            showNoData()
        }
    }

    private fun showSearchResults(list: List<Track>) {
        isHistoryVisible = false
        trackAdapter.submitTracksList(false, list)  {
            alisa show SearchResults
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.NoData -> showNoData()
            is SearchState.TrackSearchResults -> showSearchResults(state.results)
            is SearchState.TrackSearchHistory -> showSearchHistory(state.history, state.isDataSetChanged)
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
            requireActivity(),
            binding.particleView,
            bitmap,
            0f,
            viewToRemove.top.toFloat() + binding.recycler.top
        )
        binding.particleView.startAnimation { onAnimationEnd() }
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
            Debounce(Util.ANIMATION_SHORT) {
                startParticleAnimation(pos) {
                    viewModel.getHistory(false)
                    swipeHelper.enableClick()
                }
            }.start()
        }
    }

    private val btnAddToFav: () -> UnderlayButton = {
        UnderlayButton(
            requireActivity(),
            getString(R.string.history_add_to_fav),
            R.drawable.ic_add_to_fav,
            bgColor = requireActivity().getColor(R.color.greyLight),
            textColor = requireActivity().getColor(R.color.black),
        ) { pos ->
            trackAdapter.notifyItemChanged(pos)
        }
    }
}