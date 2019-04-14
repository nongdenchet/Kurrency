package com.rain.currency.domain

import com.rain.currency.data.model.Currency
import com.rain.currency.data.model.Exchange
import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.ui.converter.reducer.ConverterCommand
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.NumberFormat

class ConverterInteractor(private val currencyRepo: CurrencyRepo) {
    private val numberFormatter = NumberFormat.getNumberInstance()

    fun fetchCurrency(): Observable<ConverterCommand> {
        return Single.zip(currencyRepo.fetchExchange(), currencyRepo.fetchLastCurrency(),
                BiFunction<Exchange, Currency, ConverterData> { exchange, currency ->
                    ConverterData(exchange, currency)
                })
                .toObservable()
                .map<ConverterCommand> { ConverterCommand.CurrencyContent(it) }
                .startWith(ConverterCommand.CurrencyLoading())
                .onErrorReturn { ConverterCommand.CurrencyError(it) }
    }

    fun convertTargetValue(value: String, data: ConverterData): Single<ConverterCommand> {
        return Single.fromCallable {
            convertTarget(value, data.currency.targetUnit, data)?.let {
                return@fromCallable ConverterCommand.CurrencyResult(it)
            }
            return@fromCallable ConverterCommand.CurrencyResult(data)
        }
    }

    fun convertTargetUnit(unit: String, data: ConverterData): Single<ConverterCommand> {
        val single = Single.fromCallable {
            val newCurrency = data.currency.copy(targetUnit = unit)
            val newData = ConverterData(data.exchange, newCurrency)

            convertBase(newCurrency.base, newCurrency.baseUnit, newData)?.let {
                return@fromCallable it
            }
            return@fromCallable data
        }
        return single.observeOn(Schedulers.io())
                .doOnSuccess { currencyRepo.storeTargetUnit(it.currency.targetUnit) }
                .map { ConverterCommand.CurrencyResult(it) }
    }

    private fun convertTarget(value: String, unit: String, data: ConverterData): ConverterData? {
        val currency = data.currency
        val exchange = data.exchange

        exchange.currencies[unit]?.let { targetPrice ->
            exchange.currencies[currency.baseUnit]?.let { basePrice ->
                val result = parseNumber(value) * targetPrice / basePrice
                val newCurrency = when {
                    result > 0 -> currency.copy(base = formatValue(result), target = value, targetUnit = unit)
                    result == 0.0 -> currency.copy(base = "0", target = value, targetUnit = unit)
                    else -> currency.copy(base = "", target = value, targetUnit = unit)
                }

                return ConverterData(exchange, newCurrency)
            }
        }

        return null
    }

    fun convertBaseValue(value: String, data: ConverterData): Single<ConverterCommand> {
        return Single.fromCallable {
            convertBase(value, data.currency.baseUnit, data)?.let {
                return@fromCallable ConverterCommand.CurrencyResult(it)
            }
            return@fromCallable ConverterCommand.CurrencyResult(data)
        }
    }

    fun convertBaseUnit(unit: String, data: ConverterData): Single<ConverterCommand> {
        val single = Single.fromCallable {
            convertBase(data.currency.base, unit, data)?.let {
                return@fromCallable it
            }
            return@fromCallable data
        }
        return single.observeOn(Schedulers.io())
                .doOnSuccess { currencyRepo.storeBaseUnit(it.currency.baseUnit) }
                .map { ConverterCommand.CurrencyResult(it) }
    }

    private fun convertBase(value: String, unit: String, data: ConverterData): ConverterData? {
        val currency = data.currency
        val exchange = data.exchange

        exchange.currencies[unit]?.let { basePrice ->
            exchange.currencies[currency.targetUnit]?.let { targetPrice ->
                val result = parseNumber(value) * basePrice / targetPrice
                val newCurrency = when {
                    result > 0 -> currency.copy(base = value, target = formatValue(result), baseUnit = unit)
                    result == 0.0 -> currency.copy(base = value, target = "0", baseUnit = unit)
                    else -> currency.copy(base = value, target = "", baseUnit = unit)
                }

                return ConverterData(exchange, newCurrency)
            }
        }

        return null
    }

    private fun parseNumber(value: String): Double {
        return try {
            numberFormatter.parse(value).toString()
        } catch (e: Exception) {
            Timber.e(e)
            ""
        }.toDoubleOrNull() ?: -1.0
    }

    private fun formatValue(value: Double): String {
        val format = if (value >= 100) "%.0f" else "%.2f"
        return String.format(format, value)
    }
}
