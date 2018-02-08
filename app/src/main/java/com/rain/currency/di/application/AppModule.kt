package com.rain.currency.di.application

import com.rain.currency.di.NetworkModule
import dagger.Module

@Module(includes = arrayOf(NetworkModule::class))
class AppModule
