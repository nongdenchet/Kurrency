package com.rain.currency.support

import android.content.Context
import androidx.annotation.DrawableRes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rain.currency.R
import com.rain.currency.data.model.CurrencyInfo
import com.rain.currency.di.application.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class CurrencyMapper @Inject constructor(private val context: Context, gson: Gson, assetLoader: AssetLoader) {
    private val symbolMap: Map<String, String>

    init {
        val symbolJson = assetLoader.readTextFile("symbols.json") ?: "{}"
        val type = object : TypeToken<Map<String, String>>() {}.type
        symbolMap = gson.fromJson(symbolJson, type)
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
