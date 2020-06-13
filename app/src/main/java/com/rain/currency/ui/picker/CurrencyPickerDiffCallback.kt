package com.rain.currency.ui.picker

import androidx.recyclerview.widget.DiffUtil
import com.rain.currency.data.model.CurrencyInfo

class CurrencyPickerDiffCallback : DiffUtil.ItemCallback<CurrencyInfo>() {
    override fun areItemsTheSame(oldItem: CurrencyInfo, newItem: CurrencyInfo) =
        oldItem.unit == newItem.unit

    override fun areContentsTheSame(oldItem: CurrencyInfo, newItem: CurrencyInfo) =
        oldItem == newItem
}
