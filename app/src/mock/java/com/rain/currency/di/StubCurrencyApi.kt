package com.rain.currency.di

import android.support.v4.util.ArrayMap
import com.rain.currency.data.network.CurrencyApi
import com.rain.currency.data.network.LiveCurrency
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
