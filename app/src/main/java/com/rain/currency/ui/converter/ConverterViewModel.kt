package com.rain.currency.ui.converter

import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import com.rain.currency.data.model.Currency
import com.rain.currency.data.model.CurrencyInfo
import com.rain.currency.data.model.Exchange
import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.support.CurrencyMapper
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import timber.log.Timber

class ConverterViewModel constructor(private val currencyRepo: CurrencyRepo,
                                     private val reducer: ConverterReducer,
                                     private val currencyMapper: CurrencyMapper) {
    private val state = BehaviorRelay.createDefault(ConverterState.INIT_STATE)
    private val disposables = CompositeDisposable()

    class Input(
            val loadTrigger: Observable<Any>,
            val baseChange: Observable<String>,
            val targetChange: Observable<String>,
            val baseUnitChange: Observable<String>,
            val targetUnitChange: Observable<String>
    )

    class Output(
            val baseResult: Observable<String>,
            val targetResult: Observable<String>,
            val baseCurrency: Observable<CurrencyInfo>,
            val targetCurrency: Observable<CurrencyInfo>,
            val loading: Observable<Boolean>,
            val content: Observable<Boolean>,
            val error: Observable<Boolean>,
            val expand: Observable<Boolean>
    )

    private fun fetchCurrency(): Observable<ConverterState> {
        return Single.zip(currencyRepo.fetchExchange(), currencyRepo.fetchLastCurrency(),
                BiFunction<Exchange, Currency, ConverterState.Data> { exchange, currency ->
                    ConverterState.Data(exchange, currency)
                })
                .toObservable()
                .map { reducer.data(state.value, it) }
                .startWith(reducer.loading(state.value, true))
                .onErrorReturn { reducer.loading(state.value, false) }
    }

    fun bind(input: Input): Output {
        disposables.add(input.loadTrigger
                .switchMap { fetchCurrency() }
                .subscribe({ emitState(it) }, Timber::e))
        disposables.add(input.baseChange
                .map { reducer.changeBase(state.value, it) }
                .subscribe({ emitState(it) }, Timber::e))
        disposables.add(input.targetChange
                .map { reducer.changeTarget(state.value, it) }
                .subscribe({ emitState(it) }, Timber::e))
        disposables.add(input.baseUnitChange
                .map { reducer.changeBaseUnit(state.value, it) }
                .subscribe({ emitState(it) }, Timber::e))
        disposables.add(input.targetUnitChange
                .map { reducer.changeTargetUnit(state.value, it) }
                .subscribe({ emitState(it) }, Timber::e))

        return configureOutput()
    }

    private fun configureOutput(): Output {
        val baseResult = getData()
                .map { it.currency.base }
                .distinctUntilChanged()
        val targetResult = getData()
                .map { it.currency.target }
                .distinctUntilChanged()
        val baseCurrency = getData()
                .map { it.currency.baseUnit }
                .map { currencyMapper.toInfo(it) }
                .distinctUntilChanged()
        val targetCurrency = getData()
                .map { it.currency.targetUnit }
                .map { currencyMapper.toInfo(it) }
                .distinctUntilChanged()
        val loading = state.filter { it.expand }
                .map { it.loading }
                .distinctUntilChanged()
        val content = state.filter { it.expand }
                .map { it.data != null && !it.loading }
                .distinctUntilChanged()
        val error = state.filter { it.expand }
                .map { it.data == null && !it.loading }
                .distinctUntilChanged()
        val expand = state
                .map { it.expand }
                .distinctUntilChanged()

        return Output(baseResult, targetResult, baseCurrency, targetCurrency, loading, content, error, expand)
    }

    private fun emitState(newState: ConverterState) {
        state.accept(newState)
    }

    private fun getData(): Observable<ConverterState.Data> {
        return state.filter { it.data != null }
                .map { it.data!! }
    }

    fun unbind() {
        state.value.data?.let {
            currencyRepo.storeUserCurrencies(
                    it.currency.baseUnit,
                    it.currency.targetUnit
            )
        }
        disposables.dispose()
    }

    fun isExpand(): Boolean {
        return state.value.expand
    }

    fun setExpand(expand: Boolean) {
        Log.d("Quan", Thread.currentThread().name)
        state.accept(reducer.expand(state.value, expand))
    }
}
