package com.rain.currency.ui.picker

import com.rain.currency.di.scope.CurrencyPickerScope
import dagger.Subcomponent

@CurrencyPickerScope
@Subcomponent(modules = arrayOf(CurrencyPickerModule::class))
interface CurrencyPickerComponent {
    fun inject(currencyPicker: CurrencyPickerDialog)
}
