package com.rain.currency.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.rain.currency.data.model.Exchange
import com.rain.currency.utils.getLocale
import io.reactivex.Single

private const val BASE = "BASE"
private const val TARGET = "TARGET"
private const val DEFAULT = "USD"
private const val EXCHANGE = "EXCHANGE"

class CurrencyStore(
        private val context: Context,
        private val gson: Gson,
        private val sharedPreferences: SharedPreferences
) {
    fun storeBaseUnit(value: String) {
        sharedPreferences.edit()
                .putString(BASE, value)
                .apply()
    }

    fun storeTargetUnit(value: String) {
        sharedPreferences.edit()
                .putString(TARGET, value)
                .apply()
    }

    fun getCurrencies(): Pair<String, String> {
        return Pair(
                sharedPreferences.getString(BASE, DEFAULT),
                sharedPreferences.getString(TARGET, getLocalCurrency())
        )
    }

    fun storeExchange(exchange: Exchange) {
        sharedPreferences.edit()
                .putString(EXCHANGE, gson.toJson(exchange))
                .apply()
    }

    fun getExchange(): Single<Exchange> {
        return Single.fromCallable {
            val exchange = sharedPreferences.getString(EXCHANGE, null) ?: throw IllegalStateException()

            return@fromCallable gson.fromJson(exchange, Exchange::class.java)
        }
    }

    private fun getLocalCurrency(): String {
        val currency = java.util.Currency.getInstance(getLocale(context))
        return currency.currencyCode.toUpperCase()
    }
}
