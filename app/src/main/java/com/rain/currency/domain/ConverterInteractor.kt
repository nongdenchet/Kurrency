package com.rain.currency.domain

import timber.log.Timber
import java.text.NumberFormat

class ConverterInteractor {
    private val numberFormatter = NumberFormat.getNumberInstance()

    fun convertTarget(value: String, unit: String, data: ConverterData): ConverterData? {
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

    fun convertBase(value: String, unit: String, data: ConverterData): ConverterData? {
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
