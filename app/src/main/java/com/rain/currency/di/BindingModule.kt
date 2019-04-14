package com.rain.currency.di

import com.rain.currency.ui.converter.ConverterService
import com.rain.currency.ui.converter.di.ConverterModule
import com.rain.currency.ui.converter.di.ConverterScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BindingModule {

    @ConverterScope
    @ContributesAndroidInjector(modules = [(ConverterModule::class)])
    abstract fun contributeConverterService(): ConverterService
}
