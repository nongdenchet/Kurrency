package com.rain.currency.ui.converter.di

import com.rain.currency.data.mapper.CurrencyMapper
import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.domain.ConverterInteractor
import com.rain.currency.ui.converter.ConverterService
import com.rain.currency.ui.converter.ConverterViewModel
import com.rain.currency.ui.converter.reducer.ConverterReducer
import com.rain.currency.ui.picker.CurrencyPickerDialog
import dagger.Module
import dagger.Provides

@Module
object ConverterModule {

    @JvmStatic
    @Provides
    @ConverterScope
    fun provideConverterViewModel(
            reducer: ConverterReducer,
            interactor: ConverterInteractor,
            currencyMapper: CurrencyMapper
    ) = ConverterViewModel(reducer, interactor, currencyMapper)

    @JvmStatic
    @Provides
    @ConverterScope
    fun provideCurrencyPicker(service: ConverterService) = CurrencyPickerDialog(service)

    @JvmStatic
    @Provides
    @ConverterScope
    fun provideConverterReducer() = ConverterReducer()

    @JvmStatic
    @Provides
    @ConverterScope
    fun provideConverterInteractor(currencyRepo: CurrencyRepo) = ConverterInteractor(currencyRepo)
}
