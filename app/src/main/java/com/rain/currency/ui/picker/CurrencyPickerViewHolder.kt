package com.rain.currency.ui.picker

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.rain.currency.R
import com.rain.currency.data.model.CurrencyInfo

class CurrencyPickerViewHolder(itemView: View, onClick: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.tvUnit)
    lateinit var tvUnit: TextView
    @BindView(R.id.tvSymbol)
    lateinit var tvSymbol: TextView
    @BindView(R.id.ivIcon)
    lateinit var ivIcon: ImageView

    init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener { onClick.invoke(adapterPosition) }
    }

    fun bind(currencyInfo: CurrencyInfo) {
        currencyInfo.run {
            tvUnit.text = unit
            tvSymbol.text = symbol
            Glide.with(itemView.context)
                    .load(icon)
                    .into(ivIcon)
        }
    }
}
