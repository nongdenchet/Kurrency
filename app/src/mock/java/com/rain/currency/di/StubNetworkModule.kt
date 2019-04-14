package com.rain.currency.di

import com.rain.currency.data.api.CurrencyApi
import dagger.Module
import dagger.Provides

@Module
object StubNetworkModule {

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideCurrencyApi(): CurrencyApi = StubCurrencyApi()
}
