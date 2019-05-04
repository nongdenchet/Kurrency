package com.rain.currency.data

import android.content.Context
import android.content.SharedPreferences
import com.rain.currency.data.api.CurrencyApi
import com.rain.currency.data.local.CurrencyStore
import com.rain.currency.data.mapper.CurrencyMapper
import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.di.ApplicationScope
import com.rain.currency.support.AssetLoader
import com.rain.currency.support.NetworkManager
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
object CurrencyModule {

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideCurrencyStore(
            moshi: Moshi,
            context: Context,
            sharedPreferences: SharedPreferences
    ) = CurrencyStore(
            moshi,
            context,
            sharedPreferences
    )

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideCurrencyMapper(
            context: Context,
            moshi: Moshi,
            assetLoader: AssetLoader
    ) = CurrencyMapper(context, moshi, assetLoader)

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideCurrencyRepo(
            networkManager: NetworkManager,
            currencyApi: CurrencyApi,
            currencyStore: CurrencyStore
    ) = CurrencyRepo(
            networkManager,
            currencyApi,
            currencyStore
    )
}
