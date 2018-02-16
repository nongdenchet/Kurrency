package com.rain.currency.ui.converter

import timber.log.Timber
import java.text.NumberFormat

class ConverterReducer {
    private val numberFormatter = NumberFormat.getNumberInstance()

    private fun parseNumber(value: String): Double {
        return try {
            numberFormatter.parse(value).toString()
        } catch (e: Exception) {
            Timber.e(e)
            ""
        }.toDoubleOrNull() ?: -1.0
    }

    fun changeBase(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            convertBase(value, it.currency.baseUnit, it)?.let {
                return prev.copy(data = it)
            }
        }
        return prev
    }

    fun changeBaseUnit(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            convertBase(it.currency.base, value, it)?.let {
                return prev.copy(data = it)
            }
        }
        return prev
    }

    fun changeTarget(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            convertTarget(value, it.currency.targetUnit, it)?.let {
                return prev.copy(data = it)
            }
        }
        return prev
    }

    fun changeTargetUnit(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            convertTarget(it.currency.target, value, it)?.let {
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

    fun expand(prev: ConverterState, expand: Boolean): ConverterState {
        return prev.copy(expand = expand)
    }

    private fun formatValue(value: Double): String {
        val format = if (value >= 100) "%.0f" else "%.2f"
        return String.format(format, value)
    }

    fun data(prev: ConverterState, data: ConverterState.Data): ConverterState {
        return prev.copy(data = data, loading = false)
    }

    fun loading(prev: ConverterState, loading: Boolean): ConverterState {
        return prev.copy(loading = loading)
    }
}
