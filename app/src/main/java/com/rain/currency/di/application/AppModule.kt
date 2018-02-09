package com.rain.currency.di.application

import com.google.gson.Gson
import com.rain.currency.di.NetworkModule
import dagger.Module
import dagger.Provides

@Module(includes = arrayOf(NetworkModule::class))
class AppModule {

    @Provides
    fun provideGson(): Gson {
        return Gson()
    }
}
