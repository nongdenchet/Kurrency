package com.rain.currency.ui.converter.reducer

import com.rain.currency.domain.ConverterData

sealed class ConverterCommand {

    class CurrencyContent(val data: ConverterData) : ConverterCommand()

    class CurrencyLoading : ConverterCommand()

    class ChangeExpand(val expand: Boolean) : ConverterCommand()

    class CurrencyError(val error: Throwable) : ConverterCommand()

    class ChangeBase(val value: String) : ConverterCommand()

    class ChangeTarget(val value: String) : ConverterCommand()

    class ChangeBaseUnit(val value: String) : ConverterCommand()

    class ChangeTargetUnit(val value: String) : ConverterCommand()
}
