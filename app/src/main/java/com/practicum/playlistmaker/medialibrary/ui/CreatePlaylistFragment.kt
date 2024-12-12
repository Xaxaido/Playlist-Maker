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
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.utils.MySnackBar
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.main.domain.api.BackButtonState
import com.practicum.playlistmaker.medialibrary.ui.view_model.CreatePlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.time.Instant

class CreatePlaylistFragment: BaseFragment<FragmentCreatePlaylistBinding>() {

    private val viewModel by viewModel<CreatePlaylistViewModel>()
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
                viewModel.stateFlow.collect { title ->
                    if (title.isNotEmpty()) {
                        MySnackBar(
                            requireActivity(),
                            String.format(
                                resources.getText(R.string.playlist_created).toString(),
                                title
                            )
                        ).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                if (getPermissions(uri)) {
                    coverUri = uri
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
                coverUri?.let { saveImageToPrivateStorage(it) } ?: "",
                binding.playlistTitle.text.toString(),
                binding.description.text.toString()
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() { onBackButtonClick() }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as? BackButtonState)?.updateBackBtn(true)
        (activity as? BackButtonState)?.setCustomNavigation { onBackButtonClick() }
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
}