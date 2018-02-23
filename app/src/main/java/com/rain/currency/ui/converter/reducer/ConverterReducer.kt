package com.rain.currency.ui.converter.reducer

import com.rain.currency.ui.converter.reducer.ConverterCommand.ChangeBase
import com.rain.currency.ui.converter.reducer.ConverterCommand.ChangeBaseUnit
import com.rain.currency.ui.converter.reducer.ConverterCommand.ChangeExpand
import com.rain.currency.ui.converter.reducer.ConverterCommand.ChangeTarget
import com.rain.currency.ui.converter.reducer.ConverterCommand.ChangeTargetUnit
import com.rain.currency.ui.converter.reducer.ConverterCommand.CurrencyContent
import com.rain.currency.ui.converter.reducer.ConverterCommand.CurrencyError
import com.rain.currency.ui.converter.reducer.ConverterCommand.CurrencyLoading
import io.reactivex.functions.BiFunction
import timber.log.Timber
import java.text.NumberFormat

class ConverterReducer : BiFunction<ConverterState, ConverterCommand, ConverterState> {
    private val numberFormatter = NumberFormat.getNumberInstance()

    override fun apply(prev: ConverterState, command: ConverterCommand): ConverterState {
        return when (command) {
            is CurrencyLoading -> loading(prev, true)
            is CurrencyError -> loading(prev, false)
            is CurrencyContent -> data(prev, command.data)
            is ChangeBase -> changeBase(prev, command.value)
            is ChangeTarget -> changeTarget(prev, command.value)
            is ChangeBaseUnit -> changeBaseUnit(prev, command.value)
            is ChangeTargetUnit -> changeTargetUnit(prev, command.value)
            is ChangeExpand -> expand(prev, command.expand)
        }
    }

    private fun parseNumber(value: String): Double {
        return try {
            numberFormatter.parse(value).toString()
        } catch (e: Exception) {
            Timber.e(e)
            ""
        }.toDoubleOrNull() ?: -1.0
    }

    private fun changeBase(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            convertBase(value, it.currency.baseUnit, it)?.let {
                return prev.copy(data = it)
            }
        }
        return prev
    }

    private fun changeBaseUnit(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            convertBase(it.currency.base, value, it)?.let {
                return prev.copy(data = it)
            }
        }
        return prev
    }

    private fun changeTarget(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            convertTarget(value, it.currency.targetUnit, it)?.let {
                return prev.copy(data = it)
            }
        }
        return prev
    }

    private fun changeTargetUnit(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            val newCurrency = it.currency.copy(targetUnit = value)
            val data = ConverterState.Data(it.exchange, newCurrency)

            convertBase(newCurrency.base, newCurrency.baseUnit, data)?.let {
                return prev.copy(data = it)
            }
        }
        return prev
    }

    private fun convertTarget(value: String, unit: String, data: ConverterState.Data): ConverterState.Data? {
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

                return ConverterState.Data(exchange, newCurrency)
            }
        }

        return null
    }

    private fun convertBase(value: String, unit: String, data: ConverterState.Data): ConverterState.Data? {
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

                return ConverterState.Data(exchange, newCurrency)
            }
        }

        return null
    }

    private fun expand(prev: ConverterState, expand: Boolean): ConverterState {
        return prev.copy(expand = expand)
    }

    private fun formatValue(value: Double): String {
        val format = if (value >= 100) "%.0f" else "%.2f"
        return String.format(format, value)
    }

    private fun data(prev: ConverterState, data: ConverterState.Data): ConverterState {
        return prev.copy(data = data, loading = false)
    }

    private fun loading(prev: ConverterState, loading: Boolean): ConverterState {
        return prev.copy(loading = loading)
    }
}
