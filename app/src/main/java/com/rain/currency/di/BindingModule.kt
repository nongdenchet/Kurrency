package com.rain.currency.di

import com.rain.currency.di.scope.ConverterScope
import com.rain.currency.ui.converter.ConverterModule
import com.rain.currency.ui.converter.ConverterService
import com.rain.currency.ui.setting.SettingActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BindingModule {

    @ConverterScope
    @ContributesAndroidInjector(modules = [(ConverterModule::class)])
    abstract fun contributeConverterService(): ConverterService

    @ConverterScope
    @ContributesAndroidInjector()
    abstract fun contributeSettingActivity(): SettingActivity
}
