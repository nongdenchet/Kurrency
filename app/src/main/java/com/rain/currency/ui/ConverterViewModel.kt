package com.rain.currency.ui

import com.jakewharton.rxrelay2.BehaviorRelay
import com.rain.currency.data.repo.CurrencyRepo
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import timber.log.Timber

class ConverterViewModel constructor(private val currencyRepo: CurrencyRepo, private val reducer: ConverterReducer) {
    private val state = BehaviorRelay.createDefault(ConverterState.INIT_STATE)
    private val disposables = CompositeDisposable()

    class Input(
            val loadTrigger: Observable<Any>,
            val baseChange: Observable<String>,
            val targetChange: Observable<String>,
            val baseUnitChange: Observable<Int>,
            val targetUnitChange: Observable<Int>
    )

    class Output(
            val baseResult: Observable<String>,
            val targetResult: Observable<String>,
            val converterConfiguration: Observable<ConverterConfiguration>,
            val loading: Observable<Boolean>,
            val expand: Observable<Boolean>
    )

    private fun fetchCurrency(): Single<ConverterState.Data> {
        return Single.zip(currencyRepo.fetchExchange(), currencyRepo.fetchLastCurrency(),
                BiFunction { exchange, currency ->
                    ConverterState.Data(exchange, currency)
                })
    }

    fun bind(input: Input): Output {
        disposables.add(input.loadTrigger
                .flatMapSingle { fetchCurrency() }
                .map { ConverterState(data = it, loading = false) }
                .startWith(ConverterState(loading = true))
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
        val configuration = getData()
                .map { toConfiguration(it) }
                .distinctUntilChanged()
        val loading = state.filter { it.expand }
                .map { it.loading }
        val expand = state.map { it.expand }

        return Output(baseResult, targetResult, configuration, loading, expand)
    }

    private fun toConfiguration(data: ConverterState.Data): ConverterConfiguration {
        return ConverterConfiguration(
                data.exchange.currencies.keys.toTypedArray(),
                data.exchange.currencies.indexOfKey(data.currency.baseUnit),
                data.exchange.currencies.indexOfKey(data.currency.targetUnit)
        )
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
        state.accept(reducer.expand(state.value, expand))
    }
}
