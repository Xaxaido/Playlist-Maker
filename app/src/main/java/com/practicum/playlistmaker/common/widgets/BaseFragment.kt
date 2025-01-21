package com.practicum.playlistmaker.common.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding get() = requireNotNull(_binding) {
        "ViewBinding cannot be null"
    }

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): T
    abstract fun removeBinding()

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
        removeBinding()
        super.onDestroyView()
    }
}