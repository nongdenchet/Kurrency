package com.rain.currency.ui.picker.di

import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.data.mapper.CurrencyMapper
import com.rain.currency.ui.picker.CurrencyPickerAdapter
import com.rain.currency.ui.picker.CurrencyPickerViewModel
import dagger.Module
import dagger.Provides

@Module
object CurrencyPickerModule {

    @JvmStatic
    @Provides
    @CurrencyPickerScope
    fun provideCurrencyPickerViewModel(
        currencyRepo: CurrencyRepo,
        currencyMapper: CurrencyMapper
    ) = CurrencyPickerViewModel(currencyRepo, currencyMapper)

    @JvmStatic
    @Provides
    @CurrencyPickerScope
    fun provideCurrencyPickerAdapter() = CurrencyPickerAdapter()
}
