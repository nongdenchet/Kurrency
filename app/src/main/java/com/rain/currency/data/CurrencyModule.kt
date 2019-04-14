package com.rain.currency.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.rain.currency.data.api.CurrencyApi
import com.rain.currency.data.local.CurrencyStore
import com.rain.currency.data.mapper.CurrencyMapper
import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.di.ApplicationScope
import com.rain.currency.support.AssetLoader
import com.rain.currency.support.NetworkManager
import dagger.Module
import dagger.Provides

@Module
object CurrencyModule {

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideCurrencyStore(
            context: Context,
            gson: Gson,
            sharedPreferences: SharedPreferences
    ) = CurrencyStore(
            context,
            gson,
            sharedPreferences
    )

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideCurrencyMapper(
            context: Context,
            gson: Gson,
            assetLoader: AssetLoader
    ) = CurrencyMapper(context, gson, assetLoader)

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
