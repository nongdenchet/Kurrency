package com.rain.currency.data.local

import android.content.SharedPreferences
import com.rain.currency.di.application.ApplicationScope
import javax.inject.Inject

@ApplicationScope
open class UserCurrencyStore @Inject constructor(private val sharedPreferences: SharedPreferences) {
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
                sharedPreferences.getString(TARGET, DEFAULT)
        )
    }
}
