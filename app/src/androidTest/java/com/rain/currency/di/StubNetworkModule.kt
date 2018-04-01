package com.rain.currency.di

import com.rain.currency.data.network.CurrencyApi
import retrofit2.Retrofit

class StubNetworkModule : NetworkModule() {

    override fun provideCurrencyApi(retrofit: Retrofit): CurrencyApi {
        return StubCurrencyApi()
    }
}
