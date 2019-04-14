package com.rain.currency.di

import androidx.collection.ArrayMap
import com.rain.currency.data.api.CurrencyApi
import com.rain.currency.data.api.LiveCurrency
import io.reactivex.Single

class StubCurrencyApi: CurrencyApi {

    override fun getLiveCurrency(): Single<LiveCurrency> {
        return Single.fromCallable {
            val currencies = ArrayMap<String, Double>()
            currencies["USDUSD"] = 1.0
            currencies["USDSGD"] = 2.0

            return@fromCallable LiveCurrency(System.currentTimeMillis(), "USD", currencies)
        }
    }
}
