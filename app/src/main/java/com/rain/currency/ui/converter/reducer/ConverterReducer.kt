package com.rain.currency.ui.converter.reducer

import com.rain.currency.domain.ConverterData
import com.rain.currency.domain.ConverterInteractor
import com.rain.currency.ui.converter.reducer.ConverterCommand.ChangeBase
import com.rain.currency.ui.converter.reducer.ConverterCommand.ChangeBaseUnit
import com.rain.currency.ui.converter.reducer.ConverterCommand.ChangeExpand
import com.rain.currency.ui.converter.reducer.ConverterCommand.ChangeTarget
import com.rain.currency.ui.converter.reducer.ConverterCommand.ChangeTargetUnit
import com.rain.currency.ui.converter.reducer.ConverterCommand.CurrencyContent
import com.rain.currency.ui.converter.reducer.ConverterCommand.CurrencyError
import com.rain.currency.ui.converter.reducer.ConverterCommand.CurrencyLoading
import io.reactivex.functions.BiFunction

class ConverterReducer(private val interactor: ConverterInteractor) : BiFunction<ConverterState, ConverterCommand, ConverterState> {

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

    private fun changeBase(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            interactor.convertBase(value, it.currency.baseUnit, it)?.let {
                return prev.copy(data = it)
            }
        }

        return prev
    }

    private fun changeBaseUnit(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            interactor.convertBase(it.currency.base, value, it)?.let {
                return prev.copy(data = it)
            }
        }

        return prev
    }

    private fun changeTarget(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            interactor.convertTarget(value, it.currency.targetUnit, it)?.let {
                return prev.copy(data = it)
            }
        }

        return prev
    }

    private fun changeTargetUnit(prev: ConverterState, value: String): ConverterState {
        prev.data?.let {
            val newCurrency = it.currency.copy(targetUnit = value)
            val data = ConverterData(it.exchange, newCurrency)

            interactor.convertBase(newCurrency.base, newCurrency.baseUnit, data)?.let {
                return prev.copy(data = it)
            }
        }

        return prev
    }

    private fun expand(prev: ConverterState, expand: Boolean): ConverterState {
        return prev.copy(expand = expand)
    }

    private fun data(prev: ConverterState, data: ConverterData): ConverterState {
        val updatedData = if (prev.data != null) {
            ConverterData(exchange = data.exchange, currency = prev.data.currency)
        } else {
            data
        }
        return prev.copy(data = updatedData, loading = false)
    }

    private fun loading(prev: ConverterState, loading: Boolean): ConverterState {
        return prev.copy(loading = loading)
    }
}
