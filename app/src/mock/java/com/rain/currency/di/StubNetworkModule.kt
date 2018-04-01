package com.rain.currency.di

import com.rain.currency.data.network.CurrencyApi
import com.rain.currency.di.application.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
class StubNetworkModule {

    @Provides
    @ApplicationScope
    fun provideCurrencyApi(): CurrencyApi {
        return StubCurrencyApi()
    }
}
