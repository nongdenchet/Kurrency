package com.rain.currency.ui

import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.di.scope.ConverterScope
import dagger.Module
import dagger.Provides

@Module
class ConverterModule {

    @Provides
    @ConverterScope
    fun provideConverterViewModel(currencyRepo: CurrencyRepo, reducer: ConverterReducer): ConverterViewModel {
        return ConverterViewModel(currencyRepo, reducer)
    }

    @Provides
    @ConverterScope
    fun provideConverterReducer(): ConverterReducer {
        return ConverterReducer()
    }
}
