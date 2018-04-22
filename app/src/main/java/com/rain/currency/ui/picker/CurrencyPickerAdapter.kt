package com.rain.currency.ui.picker

import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jakewharton.rxrelay2.PublishRelay
import com.rain.currency.R
import com.rain.currency.data.model.CurrencyInfo
import io.reactivex.Observable

class CurrencyPickerAdapter : ListAdapter<CurrencyInfo, CurrencyPickerViewHolder>(CurrencyPickerDiffCallback()) {
    private val itemClicks = PublishRelay.create<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyPickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)

        return CurrencyPickerViewHolder(view) { itemClicks.accept(it) }
    }

    override fun onBindViewHolder(holder: CurrencyPickerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun itemClicks(): Observable<Int> {
        return itemClicks.hide()
    }
}
