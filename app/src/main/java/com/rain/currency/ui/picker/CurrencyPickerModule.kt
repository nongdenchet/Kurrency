package com.rain.currency.ui.picker

import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.di.scope.CurrencyPickerScope
import com.rain.currency.support.CurrencyMapper
import dagger.Module
import dagger.Provides

@Module
class CurrencyPickerModule {

    @Provides
    @CurrencyPickerScope
    fun provideCurrencyPickerViewModel(currencyRepo: CurrencyRepo,
                                       currencyMapper: CurrencyMapper): CurrencyPickerViewModel {
        return CurrencyPickerViewModel(currencyRepo, currencyMapper)
    }

    @Provides
    @CurrencyPickerScope
    fun provideCurrencyPickerAdapter(): CurrencyPickerAdapter {
        return CurrencyPickerAdapter()
    }
}