package com.rain.currency.ui

import com.rain.currency.di.activity.ActivityScope
import javax.inject.Inject

@ActivityScope
class ConverterReducer @Inject constructor() {
    fun changeBase(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            convertBase(value, it.currency.baseUnit, it)?.let {
                return prev.copy(data = it)
            }
        }
        return prev
    }

    fun changeBaseUnit(prev: ConverterState, valueIndex: Int): ConverterState {
        prev.data?.let {
            val value = it.exchange.currencies.keyAt(valueIndex)
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

    fun changeTargetUnit(prev: ConverterState, valueIndex: Int): ConverterState {
        prev.data?.let {
            val value = it.exchange.currencies.keyAt(valueIndex)
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
                val result = (value.toDoubleOrNull() ?: -1.0) * targetPrice / basePrice
                val newCurrency = if (result >= 0)
                    currency.copy(base = String.format("%.4f", result), target = value, targetUnit = unit)
                else
                    currency.copy(base = "", target = value, targetUnit = unit)

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
                val result = (value.toDoubleOrNull() ?: -1.0) * basePrice / targetPrice
                val newCurrency = if (result >= 0)
                    currency.copy(base = value, target = String.format("%.4f", result), baseUnit = unit)
                else
                    currency.copy(base = value, target = "", baseUnit = unit)

                return ConverterState.Data(exchange, newCurrency)
            }
        }

        return null
    }
}
