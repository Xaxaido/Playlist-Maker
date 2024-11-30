package com.practicum.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.MediaLibraryState
import com.practicum.playlistmaker.common.resources.VisibilityState.Loading
import com.practicum.playlistmaker.common.resources.VisibilityState.NoData
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
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.medialibrary.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.recycler.TrackAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment: BaseFragment<FragmentFavoriteTracksBinding>() {

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

    private val viewModel by viewModel<FavoriteTracksViewModel>()
    private lateinit var adapter: TrackAdapter
    private lateinit var visibility: ViewsList
    private lateinit var swipeHelper: SwipeHelper
    private var isClickEnabled = true

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setListeners()
    }

    private fun setupUI() {
        visibility = ViewsList(
            listOf(
                VisibilityItem(binding.progressBar, listOf(Loading)),
                VisibilityItem(binding.emptyMedialibrary, listOf(NoData)),
                VisibilityItem(binding.recycler, listOf(Results)),
            )
        )

        adapter = TrackAdapter(
            { track ->
                if (!isClickEnabled) return@TrackAdapter

                isClickEnabled = false
                sendToPlayer(Util.trackToJson(track))
                Debounce<Any>(Util.BUTTON_ENABLED_DELAY, lifecycleScope) { isClickEnabled = true }.start()
            },
            showFavorites = false
        )

        binding.recycler.adapter = adapter
        binding.recycler.itemAnimator = ItemAnimator()
        binding.recycler.addItemDecoration(PaddingItemDecoration(
            intArrayOf(
                0,
                resources.getDimensionPixelSize(R.dimen.toolbar_height),
            )
        ))

        swipeHelper = initSwipeHelper()
        binding.blurImageViewBottomMenu.setContentView(binding.recycler)
    }

    private fun initSwipeHelper() = object : SwipeHelper(binding.recycler) {

        override fun instantiateUnderlayButton(pos: Int): MutableList<UnderlayButton> = mutableListOf(btnDelete())
    }

    private fun sendToPlayer(json: String) {
        findNavController().navigate(
            R.id.action_send_to_player,
            PlayerFragment.createArgs(json),
        )
    }

    private fun setListeners() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun showFavoriteTracks(list: List<Track>) {
        adapter.submitTracksList(false, list, false)  {
            visibility.show(Results)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun renderState(state: MediaLibraryState) {
        when (state) {
            is MediaLibraryState.Loading -> visibility.show(Loading)
            is MediaLibraryState.Empty -> visibility.show(NoData)
            is MediaLibraryState.Content<*> -> showFavoriteTracks(state.list as List<Track>)
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
            adapter.notifyItemChanged(pos)
            Debounce<Any>(Util.ANIMATION_SHORT, viewLifecycleOwner.lifecycleScope) {
                swipeHelper.startParticleAnimation(binding.particleView, pos) {
                    viewModel.addToFavorites(adapter.getItem(pos)!!)
                    swipeHelper.enableClick()
                }
            }.start()
        }
    }
}