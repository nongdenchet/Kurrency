package com.rain.currency.data.repo

import androidx.collection.ArrayMap
import com.rain.currency.data.api.CurrencyApi
import com.rain.currency.data.api.LiveCurrency
import com.rain.currency.data.local.CurrencyStore
import com.rain.currency.data.model.Currency
import com.rain.currency.data.model.Exchange
import com.rain.currency.support.NetworkManager
import com.rain.currency.utils.exponentialBackoff
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CurrencyRepo(
    private val networkManager: NetworkManager,
    private val currencyApi: CurrencyApi,
    private val currencyStore: CurrencyStore
) {
    private var cache: Exchange? = null

    fun fetchExchange(useCache: Boolean = false): Single<Exchange> {
        if (useCache && cache != null) {
            return Single.just(cache)
        }

        return getCurrency().doOnSuccess { cache = it }
            .subscribeOn(Schedulers.io())
    }

    private fun getCurrency(): Single<Exchange> {
        return if (networkManager.isNetworkAvailable()) {
            getRemoteCurrency().onErrorResumeNext { getLocalCurrency() }
        } else {
            getLocalCurrency()
        }
    }

    private fun getLocalCurrency(): Single<Exchange> {
        return currencyStore.getExchange()
    }

    private fun getRemoteCurrency(): Single<Exchange> {
        return currencyApi.getLiveCurrency()
            .retryWhen(exponentialBackoff(3, 1))
            .map { toExchange(it) }
            .doOnSuccess { currencyStore.storeExchange(it) }
    }

    fun storeBaseUnit(value: String) = currencyStore.storeBaseUnit(value)

    fun storeTargetUnit(value: String) = currencyStore.storeTargetUnit(value)

    private fun toExchange(liveCurrency: LiveCurrency): Exchange {
        val currencies = ArrayMap<String, Double>(liveCurrency.quotes.size)
        currencies[liveCurrency.source] = 1.0
        for (currency in liveCurrency.quotes) {
            currencies[currency.key.substring(3)] = 1.0 / currency.value
        }

        return Exchange(currencies)
    }

    fun fetchLastCurrency(): Single<Currency> {
        return Single.just(currencyStore.getCurrencies())
            .map { Currency(baseUnit = it.first, targetUnit = it.second) }
    }
}
