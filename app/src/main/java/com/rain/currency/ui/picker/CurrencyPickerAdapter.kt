package com.rain.currency.ui.picker

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.jakewharton.rxrelay2.PublishRelay
import com.rain.currency.R
import com.rain.currency.data.model.CurrencyInfo
import io.reactivex.Observable

class CurrencyPickerAdapter : RecyclerView.Adapter<CurrencyPickerAdapter.CurrencyPickerViewHolder>() {
    private val itemClicks = PublishRelay.create<Int>()
    private var items = emptyList<CurrencyInfo>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyPickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)

        return CurrencyPickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyPickerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateItems(newItems: List<CurrencyInfo>) {
        val diffResult = DiffUtil.calculateDiff(CurrencyPickerDIffCallback(items, newItems))
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun itemClicks(): Observable<Int> {
        return itemClicks.hide()
    }

    inner class CurrencyPickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.tvUnit)
        lateinit var tvUnit: TextView
        @BindView(R.id.tvSymbol)
        lateinit var tvSymbol: TextView
        @BindView(R.id.ivIcon)
        lateinit var ivIcon: ImageView

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener {
                itemClicks.accept(adapterPosition)
            }
        }

        fun bind(currencyInfo: CurrencyInfo) {
            currencyInfo.run {
                tvUnit.text = unit
                tvSymbol.text = symbol
                Glide.with(itemView.context)
                        .load(currencyInfo.icon)
                        .into(ivIcon)
            }
        }
    }
}
