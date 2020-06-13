package com.rain.currency.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.rain.currency.data.model.Exchange
import com.rain.currency.utils.getLocale
import com.squareup.moshi.Moshi
import io.reactivex.Single

private const val BASE = "BASE"
private const val TARGET = "TARGET"
private const val DEFAULT = "USD"
private const val EXCHANGE = "EXCHANGE"

class CurrencyStore(
    moshi: Moshi,
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {
    private val adapter = moshi.adapter(Exchange::class.java)

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
            sharedPreferences.getString(BASE, DEFAULT) ?: DEFAULT,
            sharedPreferences.getString(TARGET, getLocalCurrency()) ?: getLocalCurrency()
        )
    }

    fun storeExchange(exchange: Exchange) {
        sharedPreferences.edit()
            .putString(EXCHANGE, adapter.toJson(exchange))
            .apply()
    }

    fun getExchange(): Single<Exchange> {
        return Single.fromCallable {
            val exchange = sharedPreferences.getString(EXCHANGE, null)
                ?: throw IllegalStateException()

            return@fromCallable adapter.fromJson(exchange)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getLocalCurrency(): String {
        val currency = java.util.Currency.getInstance(getLocale(context))
        return currency.currencyCode.toUpperCase()
    }
}
