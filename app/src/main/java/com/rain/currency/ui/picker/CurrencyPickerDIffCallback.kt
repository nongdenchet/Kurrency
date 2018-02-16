package com.rain.currency.ui.picker

import android.support.v7.util.DiffUtil
import com.rain.currency.data.model.CurrencyInfo

class CurrencyPickerDIffCallback(private val oldList: List<CurrencyInfo>,
                                 private val newList: List<CurrencyInfo>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition].unit == newList[newItemPosition].unit

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition] == newList[newItemPosition]
}
