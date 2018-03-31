package com.rain.currency.ui.converter

import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.di.scope.ConverterScope
import com.rain.currency.domain.ConverterInteractor
import com.rain.currency.support.CurrencyMapper
import com.rain.currency.ui.converter.reducer.ConverterReducer
import com.rain.currency.ui.picker.CurrencyPickerDialog
import dagger.Module
import dagger.Provides

@Module
class ConverterModule {

    @Provides
    @ConverterScope
    fun provideConverterViewModel(reducer: ConverterReducer,
                                  interactor: ConverterInteractor,
                                  currencyMapper: CurrencyMapper): ConverterViewModel {
        return ConverterViewModel(reducer, interactor, currencyMapper)
    }

    @Provides
    @ConverterScope
    fun provideCurrencyPicker(service: ConverterService): CurrencyPickerDialog {
        return CurrencyPickerDialog(service)
    }

    @Provides
    @ConverterScope
    fun provideConverterReducer(): ConverterReducer {
        return ConverterReducer()
    }

    @Provides
    @ConverterScope
    fun provideConverterInteractor(currencyRepo: CurrencyRepo): ConverterInteractor {
        return ConverterInteractor(currencyRepo)
    }
}
