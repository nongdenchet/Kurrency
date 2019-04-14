package com.rain.currency.ui.converter.reducer

import com.rain.currency.domain.ConverterData
import com.rain.currency.ui.converter.reducer.ConverterCommand.ChangeExpand
import com.rain.currency.ui.converter.reducer.ConverterCommand.CurrencyContent
import com.rain.currency.ui.converter.reducer.ConverterCommand.CurrencyError
import com.rain.currency.ui.converter.reducer.ConverterCommand.CurrencyLoading
import com.rain.currency.ui.converter.reducer.ConverterCommand.CurrencyResult
import io.reactivex.functions.BiFunction
import timber.log.Timber

class ConverterReducer : BiFunction<ConverterState, ConverterCommand, ConverterState> {
    override fun apply(prev: ConverterState, command: ConverterCommand): ConverterState {
        return when (command) {
            is CurrencyLoading -> loading(prev, true)
            is CurrencyError -> error(prev, command.error)
            is CurrencyResult -> result(prev, command.data)
            is CurrencyContent -> data(prev, command.data)
            is ChangeExpand -> expand(prev, command.expand)
        }
    }

    private fun error(prev: ConverterState, error: Throwable): ConverterState {
        Timber.e(error)
        return loading(prev, false)
    }

    private fun result(prev: ConverterState, data: ConverterData): ConverterState {
        return prev.copy(data = data)
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
