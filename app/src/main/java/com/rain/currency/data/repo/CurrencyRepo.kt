package com.rain.currency.data.repo

import android.support.v4.util.ArrayMap
import com.rain.currency.data.model.Currency
import com.rain.currency.data.model.Exchange
import io.reactivex.Single
import java.util.concurrent.TimeUnit

open class CurrencyRepo {
    open fun fetchExchange(): Single<Exchange> {
        val currencies = ArrayMap<String, Double>()
        currencies["USD"] = 1.0
        currencies["VND"] = 1.0 / 20000

        return Single.fromCallable {
            return@fromCallable Exchange("USD", currencies)
        }.delay(1, TimeUnit.SECONDS)
    }

    open fun fetchLastCurrency(): Single<Currency> {
        return Single.just(Pair("USD", "VND"))
                .map { Currency(baseUnit = it.first, targetUnit = it.second) }
    }
}
