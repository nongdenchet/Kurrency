package com.rain.currency.data.repo

import android.support.v4.util.ArrayMap
import com.rain.currency.data.model.Currency
import com.rain.currency.data.model.Exchange
import com.rain.currency.data.network.CurrencyApi
import com.rain.currency.data.network.LiveCurrency
import com.rain.currency.di.application.ApplicationScope
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.Date
import javax.inject.Inject

@ApplicationScope
open class CurrencyRepo @Inject constructor(private val currencyApi: CurrencyApi) {

    open fun fetchExchange(): Single<Exchange> {
        return currencyApi.getLiveCurrency()
                .map { toExchange(it) }
                .subscribeOn(Schedulers.io())
    }

    private fun toExchange(liveCurrency: LiveCurrency): Exchange {
        val currencies = ArrayMap<String, Double>(liveCurrency.quotes.size)
        currencies[liveCurrency.source] = 1.0
        for (currency in liveCurrency.quotes) {
            currencies[currency.key.substring(3)] = 1.0 / currency.value
        }
        return Exchange(liveCurrency.source, Date(liveCurrency.timestamp), currencies)
    }

    open fun fetchLastCurrency(): Single<Currency> {
        return Single.just(Pair("SGD", "VND"))
                .map { Currency(baseUnit = it.first, targetUnit = it.second) }
    }
}
