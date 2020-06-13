package com.rain.currency.ui.converter.reducer

import com.rain.currency.domain.ConverterData

sealed class ConverterCommand {

    class CurrencyContent(val data: ConverterData) : ConverterCommand()

    class CurrencyResult(val data: ConverterData) : ConverterCommand()

    object CurrencyLoading : ConverterCommand()

    class ChangeExpand(val expand: Boolean) : ConverterCommand()

    class CurrencyError(val error: Throwable) : ConverterCommand()
}
