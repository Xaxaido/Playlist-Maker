package com.practicum.playlistmaker.common.widgets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.practicum.playlistmaker.R

abstract class BaseBottomDialogFragment<T : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: T? = null
    protected val binding get() = requireNotNull(_binding) {
        "ViewBinding cannot be null"
    }

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.attributes?.windowAnimations = R.style.BottomDialogAnimations
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}