package com.rain.currency.ui.picker.di

import com.rain.currency.ui.picker.CurrencyPickerDialog
import dagger.Component

@CurrencyPickerScope
@Component(
    modules = [CurrencyPickerModule::class],
    dependencies = [CurrencyPickerDependencies::class]
)
interface CurrencyPickerComponent {

    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: CurrencyPickerDependencies): Builder

        fun build(): CurrencyPickerComponent
    }

    fun inject(currencyPicker: CurrencyPickerDialog)
}
