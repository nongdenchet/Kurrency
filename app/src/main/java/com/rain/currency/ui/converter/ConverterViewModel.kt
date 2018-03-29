package com.rain.currency.ui.converter

import android.view.View
import com.jakewharton.rxrelay2.BehaviorRelay
import com.rain.currency.data.model.Currency
import com.rain.currency.data.model.CurrencyInfo
import com.rain.currency.data.model.Exchange
import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.domain.ConverterData
import com.rain.currency.support.CurrencyMapper
import com.rain.currency.ui.converter.reducer.ConverterCommand
import com.rain.currency.ui.converter.reducer.ConverterReducer
import com.rain.currency.ui.converter.reducer.ConverterState
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ConverterViewModel constructor(private val currencyRepo: CurrencyRepo,
                                     private val reducer: ConverterReducer,
                                     private val currencyMapper: CurrencyMapper) {
    private val state = BehaviorRelay.createDefault(ConverterState.INIT_STATE)
    private var disposable: Disposable? = null

    class Input(
            val retryClicks: Observable<Any>,
            val moneyClicks: Observable<Any>,
            val baseChange: Observable<String>,
            val targetChange: Observable<String>,
            val baseUnitChange: Observable<String>,
            val targetUnitChange: Observable<String>,
            val backClicks: Observable<Any>
    )

    class Output(
            val baseResult: Observable<String>,
            val targetResult: Observable<String>,
            val baseCurrency: Observable<CurrencyInfo>,
            val targetCurrency: Observable<CurrencyInfo>,
            val loadingVisibility: Observable<Int>,
            val showContent: Observable<Boolean>,
            val errorVisibility: Observable<Int>,
            val expand: Observable<Boolean>
    )

    private fun fetchCurrency(): Observable<ConverterCommand> {
        return Single.zip(currencyRepo.fetchExchange(), currencyRepo.fetchLastCurrency(),
                BiFunction<Exchange, Currency, ConverterData> { exchange, currency ->
                    ConverterData(exchange, currency)
                })
                .toObservable()
                .map<ConverterCommand> { ConverterCommand.CurrencyContent(it) }
                .startWith(ConverterCommand.CurrencyLoading())
                .onErrorReturn { ConverterCommand.CurrencyError(it) }
    }

    fun bind(input: Input): Output {
        val moneyClicks = input.moneyClicks.share()
        val expandChange = Observable.merge(
                moneyClicks.map { true },
                input.backClicks.withLatestFrom(state.filter { it.expand },
                        BiFunction { _, _ -> false })
        )
        val loadTrigger = Observable.merge(input.retryClicks, moneyClicks)
                .startWith(0)

        val commands = Observable.merge(listOf(
                expandChange.map { ConverterCommand.ChangeExpand(it) },
                loadTrigger.switchMap { fetchCurrency() },
                input.baseChange.map { ConverterCommand.ChangeBase(it) },
                input.targetChange.map { ConverterCommand.ChangeTarget(it) },
                input.baseUnitChange.map { ConverterCommand.ChangeBaseUnit(it) },
                input.targetUnitChange.map { ConverterCommand.ChangeTargetUnit(it) }
        ))

        disposable = commands.scan(ConverterState.INIT_STATE, reducer)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ state.accept(it) }, Timber::e)

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
                .observeOn(Schedulers.io())
                .doOnNext { currencyRepo.storeBaseUnit(it.unit) }
        val targetCurrency = getData()
                .map { it.currency.targetUnit }
                .map { currencyMapper.toInfo(it) }
                .distinctUntilChanged()
                .observeOn(Schedulers.io())
                .doOnNext { currencyRepo.storeTargetUnit(it.unit) }
        val loadingVisibility = state.filter { it.expand }
                .map { it.loading }
                .map { if (it) View.VISIBLE else View.GONE }
                .distinctUntilChanged()
        val showContent = state.filter { it.expand }
                .map { it.data != null && !it.loading }
                .distinctUntilChanged()
        val errorVisibility = state.filter { it.expand }
                .map { it.data == null && !it.loading }
                .map { if (it) View.VISIBLE else View.GONE }
                .distinctUntilChanged()
        val expand = state
                .map { it.expand }
                .distinctUntilChanged()

        return Output(baseResult, targetResult, baseCurrency, targetCurrency,
                loadingVisibility, showContent, errorVisibility, expand)
    }

    private fun getData(): Observable<ConverterData> {
        return state.filter { it.data != null }
                .map { it.data!! }
    }

    fun unbind() {
        disposable?.dispose()
        disposable = null
    }
}
