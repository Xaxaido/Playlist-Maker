package com.practicum.playlistmaker.presentation.search

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ItemFooterBinding

class FooterViewHolder(
    private val binding: ItemFooterBinding,
    private var onClearHistoryBtnClick: () -> Unit = {},
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(isVisible: Boolean) {
        itemView.isVisible = isVisible
        binding.btnClearHistory.setOnClickListener { onClearHistoryBtnClick() }
    }
}