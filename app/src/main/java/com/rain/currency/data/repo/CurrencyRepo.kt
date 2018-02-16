package com.rain.currency.data.repo

import android.support.v4.util.ArrayMap
import com.rain.currency.data.local.UserCurrencyStore
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
open class CurrencyRepo @Inject constructor(
        private val currencyApi: CurrencyApi,
        private val userCurrencyStore: UserCurrencyStore
) {
    private var cache: Exchange? = null

    open fun fetchExchange(useCache: Boolean = false): Single<Exchange> {
        if (useCache && cache != null) {
            return Single.just(cache)
        }
        return currencyApi.getLiveCurrency()
                .map { toExchange(it) }
                .doOnSuccess { cache = it }
                .subscribeOn(Schedulers.io())
    }

    open fun storeUserCurrencies(baseUnit: String, targetUnit: String) {
        userCurrencyStore.storeCurrencies(baseUnit, targetUnit)
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
        return Single.just(userCurrencyStore.getCurrencies())
                .map { Currency(baseUnit = it.first, targetUnit = it.second) }
    }
}
