package com.rain.currency.data.mapper

import android.content.Context
import androidx.annotation.DrawableRes
import com.rain.currency.R
import com.rain.currency.data.model.CurrencyInfo
import com.rain.currency.support.AssetLoader
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class CurrencyMapper(
    private val context: Context,
    private val moshi: Moshi,
    private val assetLoader: AssetLoader
) {
    private val symbolMap: Map<String, String> by lazy {
        val symbolJson = assetLoader.readTextFile("symbols.json") ?: "{}"
        val type =
            Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        val adapter = moshi.adapter<Map<String, String>>(type)
        adapter.fromJson(symbolJson) ?: emptyMap()
    }

    fun toInfo(value: String): CurrencyInfo {
        return CurrencyInfo(
            value,
            toSymbol(value),
            toIcon(value)
        )
    }

    private fun toSymbol(value: String): String {
        return symbolMap[value] ?: "$"
    }

    @DrawableRes
    private fun toIcon(value: String): Int {
        val iconName = "ic_${value.toLowerCase()}"
        val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)

        return if (resId != 0)
            resId
        else
            R.drawable.ic_country_placeholder
    }
}
