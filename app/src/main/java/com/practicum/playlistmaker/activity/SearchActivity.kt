package com.practicum.playlistmaker.activity

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
import com.practicum.playlistmaker.data.domain.model.Track
import com.practicum.playlistmaker.data.domain.repository.PrefsStorageRepositoryImpl
import com.practicum.playlistmaker.data.resources.VisibilityState.Error
import com.practicum.playlistmaker.data.resources.VisibilityState.History
import com.practicum.playlistmaker.data.resources.VisibilityState.Loading
import com.practicum.playlistmaker.data.resources.VisibilityState.NoData
import com.practicum.playlistmaker.data.resources.VisibilityState.NothingFound
import com.practicum.playlistmaker.data.resources.VisibilityState.SearchResults
import com.practicum.playlistmaker.data.resources.VisibilityState.ViewsList
import com.practicum.playlistmaker.data.resources.VisibilityState.VisibilityItem
import com.practicum.playlistmaker.data.source.TrackAdapter
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.extension.network.RetrofitService
import com.practicum.playlistmaker.extension.network.TrackResponse
import com.practicum.playlistmaker.extension.util.Debounce
import com.practicum.playlistmaker.extension.util.Util
import com.practicum.playlistmaker.extension.widget.StickyView
import com.practicum.playlistmaker.player.ui.PlayerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private var searchRequest = ""
    private var isHistoryShowed = true
    private var isClickEnabled = true
    private var isKeyboardVisible = false
    private lateinit var stickyView: StickyView
    private val timer = Debounce(delay = Util.USER_INPUT_DELAY) { searchTracks() }
    private val prefs: PrefsStorageRepositoryImpl by lazy {
        PrefsStorageRepositoryImpl(applicationContext)
    }
    private val alisa: ViewsList by lazy {
        ViewsList(
            listOf(
                VisibilityItem(binding.networkFailure, listOf(Error)),
                VisibilityItem(binding.nothingFound, listOf(NothingFound)),
                VisibilityItem(binding.searchHistoryHeader, listOf(History)),
                VisibilityItem(binding.clearHistory, listOf(History)),
                VisibilityItem(binding.progressBar, listOf(Loading)),
                VisibilityItem(binding.recycler, listOf(History, SearchResults)),
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
        binding.recycler.itemAnimator = null
        setupUI()
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

    private fun setupUI() {
        stickyView = StickyView(binding.recycler, binding.clearHistory)
    }

    private fun setListeners() {
        updateKeyboardVisibility()
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0 && isKeyboardVisible) hideKeyboard()
                if (binding.clearHistory.isVisible && !isKeyboardVisible) stickyView.update()
            }
        })

        trackAdapter.setOnTrackClickListener { track ->
            if (!isClickEnabled) return@setOnTrackClickListener
            prefs.addTrack(track)
            sendToPlayer(track)
            isClickEnabled = false
            Debounce(delay = Util.BUTTON_ENABLED_DELAY) { isClickEnabled = true }.start()
        }

        binding.btnClearHistory.setOnClickListener {
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
        updateData(emptyList()) {
            alisa show NoData
        }
    }

    private fun showHistory(list: List<Track>) {
        isHistoryShowed = true
        list.apply {
            if (this.isNotEmpty()) {
                updateData(this) {
                    alisa show History
                    stickyView.reset()
                    stickyView.addRoom()
                }
            } else showNoData()
        }
    }

    private fun showContent(list: List<Track>) {
        isHistoryShowed = false
        updateData(list) {
            alisa show SearchResults
            if (binding.recycler.itemDecorationCount > 0) binding.recycler.removeItemDecorationAt(0)
        }
    }

    private fun updateData(list: List<Track>, onFinish: () -> Unit) {
        trackAdapter.submitTracksList(list, onFinish)
    }

    private fun searchTracks() {
        if (searchRequest.isEmpty()) return

        alisa show Loading
        RetrofitService.iTunes?.search(searchRequest)?.enqueue(object : Callback<TrackResponse> {

            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                response.body()?.let {
                    if (it.results.isEmpty()) {
                        alisa show NothingFound
                    } else {
                        showContent(it.results)
                    }
                } ?: (alisa show Error)
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                alisa show Error
            }
        })
    }
}