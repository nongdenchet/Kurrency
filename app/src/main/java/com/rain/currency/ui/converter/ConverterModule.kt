package com.rain.currency.ui.converter

import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.di.scope.ConverterScope
import com.rain.currency.support.CurrencyMapper
import com.rain.currency.ui.converter.reducer.ConverterReducer
import com.rain.currency.ui.picker.CurrencyPicker
import dagger.Module
import dagger.Provides

@Module
class ConverterModule {

    @Provides
    @ConverterScope
    fun provideConverterViewModel(currencyRepo: CurrencyRepo,
                                  reducer: ConverterReducer,
                                  currencyMapper: CurrencyMapper): ConverterViewModel {
        return ConverterViewModel(currencyRepo, reducer, currencyMapper)
    }

    @Provides
    @ConverterScope
    fun provideCurrencyPicker(service: ConverterService): CurrencyPicker {
        return CurrencyPicker(service)
    }

    @Provides
    @ConverterScope
    fun provideConverterReducer(): ConverterReducer {
        return ConverterReducer()
    }
}
