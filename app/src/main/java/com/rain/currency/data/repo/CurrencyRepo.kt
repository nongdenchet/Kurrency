package com.rain.currency.data.repo

import android.support.v4.util.ArrayMap
import com.rain.currency.data.local.UserCurrencyStore
import com.rain.currency.data.model.Currency
import com.rain.currency.data.model.Exchange
import com.rain.currency.data.network.CurrencyApi
import com.rain.currency.data.network.LiveCurrency
import com.rain.currency.di.application.ApplicationScope
import com.rain.currency.utils.exponentialBackoff
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.Date
import javax.inject.Inject

@ApplicationScope
class CurrencyRepo @Inject constructor(
        private val currencyApi: CurrencyApi,
        private val userCurrencyStore: UserCurrencyStore
) {
    private var cache: Exchange? = null

    fun fetchExchange(useCache: Boolean = false): Single<Exchange> {
        if (useCache && cache != null) {
            return Single.just(cache)
        }
        return currencyApi.getLiveCurrency()
                .retryWhen(exponentialBackoff(3, 3))
                .map { toExchange(it) }
                .doOnSuccess { cache = it }
                .subscribeOn(Schedulers.io())
    }

    fun storeBaseUnit(value: String) = userCurrencyStore.storeBaseUnit(value)

    fun storeTargetUnit(value: String) = userCurrencyStore.storeTargetUnit(value)

    private fun toExchange(liveCurrency: LiveCurrency): Exchange {
        val currencies = ArrayMap<String, Double>(liveCurrency.quotes.size)
        currencies[liveCurrency.source] = 1.0
        for (currency in liveCurrency.quotes) {
            currencies[currency.key.substring(3)] = 1.0 / currency.value
        }
        return Exchange(liveCurrency.source, Date(liveCurrency.timestamp), currencies)
    }

    fun fetchLastCurrency(): Single<Currency> {
        return Single.just(userCurrencyStore.getCurrencies())
                .map { Currency(baseUnit = it.first, targetUnit = it.second) }
    }
}
