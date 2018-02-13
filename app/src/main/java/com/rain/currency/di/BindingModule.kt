package com.rain.currency.di

import com.rain.currency.di.activity.ActivityScope
import com.rain.currency.service.CurrencyAccessibilityService
import com.rain.currency.ui.ConverterActivity
import com.rain.currency.ui.ConverterModule
import com.rain.currency.ui.ConverterService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(ConverterModule::class))
    abstract fun contributeConverterActivity(): ConverterActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(ConverterModule::class))
    abstract fun contributeConverterService(): ConverterService

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeCurrencyAccessibilityService(): CurrencyAccessibilityService
}
