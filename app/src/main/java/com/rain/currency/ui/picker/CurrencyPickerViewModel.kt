package com.rain.currency.ui.picker

import com.jakewharton.rxrelay2.BehaviorRelay
import com.rain.currency.data.mapper.CurrencyMapper
import com.rain.currency.data.model.CurrencyInfo
import com.rain.currency.data.repo.CurrencyRepo
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import timber.log.Timber

class CurrencyPickerViewModel(
        private val currencyRepo: CurrencyRepo,
        private val currencyMapper: CurrencyMapper
) {
    private val currencies = BehaviorRelay.create<List<CurrencyInfo>>()
    private val disposables = CompositeDisposable()

    class Input(
            val currencyType: CurrencyType,
            val keyword: Observable<String>,
            val itemSelection: Observable<Int>
    )

    class Output(
            val currencies: Observable<List<CurrencyInfo>>,
            val result: Observable<Pair<CurrencyType, CurrencyInfo>>
    )

    fun bind(input: Input): Output {
        disposables.add(handleKeyword(input.keyword))

        val result = input.itemSelection
                .filter { it < currencies.value.size }
                .map { currencies.value[it] }
                .map { Pair(input.currencyType, it) }

        return Output(currencies.hide(), result)
    }

    fun unbind() {
        disposables.clear()
    }

    private fun getUnits(): Observable<List<String>> {
        return currencyRepo.fetchExchange(true)
                .map { it.currencies.keys }
                .map { it.toList() }
                .onErrorReturn { emptyList() }
                .toObservable()
    }

    private fun keywords(keywordStream: Observable<String>): Observable<String> {
        return keywordStream.map { it.toUpperCase() }
                .map { it.trim() }
                .startWith("")
    }

    private fun handleKeyword(keywordStream: Observable<String>): Disposable {
        return Observable.combineLatest(
                keywords(keywordStream),
                getUnits(),
                BiFunction<String, List<String>, List<CurrencyInfo>> { keyword, currencies ->
                    combine(keyword, currencies)
                })
                .subscribe({ currencies.accept(it) }, Timber::e)
    }

    private fun combine(keyword: String, currencies: List<String>): List<CurrencyInfo> {
        return currencies.asSequence()
                .filter { it.contains(keyword) }
                .map { currencyMapper.toInfo(it) }
                .toList()
    }
}
