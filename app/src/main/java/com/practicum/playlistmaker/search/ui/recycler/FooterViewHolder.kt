package com.practicum.playlistmaker.search.ui.recycler

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.databinding.ItemFooterBinding

class FooterViewHolder(
    private val binding: ItemFooterBinding,
    private var onClearHistoryBtnClick: () -> Unit = {},
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(isVisible: Boolean) {
        itemView.postDelayed(
            { binding.clearHistory.isVisible = isVisible },
            if (isVisible) 0L else Util.ANIMATION_SHORT
        )

        binding.btnClearHistory.setOnClickListener { onClearHistoryBtnClick() }
    }
}