package com.rain.currency.di.application

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
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

    @Provides
    @ApplicationScope
    fun provideSharePreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName, MODE_PRIVATE)
    }
}
