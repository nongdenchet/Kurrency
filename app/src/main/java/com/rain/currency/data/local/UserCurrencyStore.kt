package com.rain.currency.data.local

import android.content.Context
import android.content.SharedPreferences
import com.rain.currency.di.application.ApplicationScope
import com.rain.currency.utils.getLocale
import javax.inject.Inject

@ApplicationScope
open class UserCurrencyStore @Inject constructor(
        private val context: Context,
        private val sharedPreferences: SharedPreferences
) {

    companion object {
        val BASE = "BASE"
        val TARGET = "TARGET"
        val DEFAULT = "USD"
    }

    open fun storeCurrencies(base: String, target: String) {
        sharedPreferences.edit()
                .putString(BASE, base)
                .putString(TARGET, target)
                .apply()
    }

    open fun getCurrencies(): Pair<String, String> {
        return Pair(
                sharedPreferences.getString(BASE, DEFAULT),
                sharedPreferences.getString(TARGET, getLocalCurrency())
        )
    }

    private fun getLocalCurrency(): String {
        val currency = java.util.Currency.getInstance(getLocale(context))
        return currency.currencyCode.toUpperCase()
    }
}
