package com.practicum.playlistmaker.medialibrary.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.CreatePlaylistState
import com.practicum.playlistmaker.common.utils.MySnackBar
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.main.domain.api.BackButtonState
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.ui.view_model.CreatePlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream
import java.time.Instant

class CreatePlaylistFragment: BaseFragment<FragmentCreatePlaylistBinding>() {

    companion object {
        private const val ARGS_CREATE_PLAYLIST = "ARGS_CREATE_PLAYLIST"
        private var playlist: Playlist? = null

        fun createArgs(playlist: Playlist?): Bundle {
            val json = Gson().toJson(playlist)
            this.playlist = playlist
            return bundleOf(ARGS_CREATE_PLAYLIST to json)
        }
    }

    private val viewModel by viewModel<CreatePlaylistViewModel> {
        parametersOf(requireArguments().getString(ARGS_CREATE_PLAYLIST))
    }
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var coverUri: Uri? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setListeners()
    }

    private fun setupUI() {
        Util.drawFrame(binding.imagePicker, resources.displayMetrics.density, ContextCompat.getColor(requireActivity(), R.color.greyMedium))

        confirmDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(resources.getString(R.string.create_playlist_dialog_title))
            .setMessage(resources.getString(R.string.create_playlist_dialog_message))
            .setNeutralButton(resources.getString(R.string.create_playlist_dialog_cancel)) { _, _ ->
            }.setPositiveButton(resources.getString(R.string.create_playlist_dialog_finish)) { _, _ ->
                findNavController().navigateUp()
            }
    }

    private fun setListeners() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect {
                    renderState(it)
                }
            }
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                if (getPermissions(uri)) {
                    coverUri = uri
                    playlist?.cover = null
                    binding.imagePicker.setImageURI(uri)
                }
            }
        }

        binding.imagePicker.setOnClickListener {
            pickMedia.launch(arrayOf("image/*"))
        }

        binding.playlistTitle.doOnTextChanged { text, _, _, _ ->
            val title = text.toString()
            binding.buttonCreatePlaylist.isEnabled = title.isNotEmpty()
        }

        binding.buttonCreatePlaylist.setOnClickListener {
            viewModel.createPlaylist(
                playlist?.id,
                playlist?.cover ?: coverUri?.let { saveImageToPrivateStorage(it) } ?: "",
                binding.playlistTitle.text.toString(),
                binding.description.text.toString(),
                playlist?.tracks,
                playlist?.tracksCount,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? BackButtonState)?.updateBackBtn(true)
        if (playlist == null) (activity as? BackButtonState)?.setCustomNavigation { onBackButtonClick() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? BackButtonState)?.customNavigateAction = null
    }

    private fun onBackButtonClick(): Boolean {
        return if (binding.playlistTitle.text.toString().isEmpty() && binding.description.text.toString().isEmpty()) {
            findNavController().navigateUp()
        } else {
            confirmDialog.show()
            false
        }
    }

    private fun getPermissions(uri: Uri): Boolean {
        val contentResolver = requireActivity().contentResolver
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        return try {
            contentResolver.takePersistableUriPermission(uri, takeFlags)
            true
        } catch(e: SecurityException) {
            false
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")

        if (!filePath.exists()){
            filePath.mkdirs()
        }

        val fileName = "cover_${Instant.now().toEpochMilli()}.jpg"
        val file = File(filePath, fileName)
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

        return file.path
    }

    private fun createPlaylist(title: String) {
        if (title.isNotEmpty()) {

            if (playlist == null) {
                MySnackBar(
                    requireView(),
                    String.format(
                        resources.getText(R.string.playlist_created).toString(),
                        title
                    )
                ).show()
            }

            findNavController().popBackStack()
        }
    }

    private fun editPlaylist(playlist: Playlist) {
        (activity as? BackButtonState)?.setTitle(getString(R.string.edit))

        with (binding) {
            Glide.with(this@CreatePlaylistFragment)
                .load(playlist.cover)
                .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.album_cover_stub))
                .centerCrop()
                .into(imagePicker)

            playlistTitle.setText(playlist.name)
            description.setText(playlist.description)
            buttonCreatePlaylist.text = getString(R.string.save)
        }
    }

    private fun renderState(state: CreatePlaylistState) {
        when (state) {
            is CreatePlaylistState.Create -> createPlaylist(state.title)
            is CreatePlaylistState.Edit -> editPlaylist(state.playlist)
        }

    }
}