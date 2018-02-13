package com.rain.currency.di

import com.rain.currency.di.scope.ConverterScope
import com.rain.currency.service.CurrencyAccessibilityService
import com.rain.currency.ui.ConverterModule
import com.rain.currency.ui.ConverterService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BindingModule {

    @ConverterScope
    @ContributesAndroidInjector(modules = arrayOf(ConverterModule::class))
    abstract fun contributeConverterService(): ConverterService

    @ConverterScope
    @ContributesAndroidInjector
    abstract fun contributeCurrencyAccessibilityService(): CurrencyAccessibilityService
}
