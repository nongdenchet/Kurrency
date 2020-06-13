package com.rain.currency

import android.app.Application
import com.rain.currency.di.AppComponent
import com.rain.currency.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class CurrencyApp : Application(), HasAndroidInjector {
    lateinit var component: AppComponent

    @Inject
    lateinit var dispatchingInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispatchingInjector

    override fun onCreate() {
        super.onCreate()
        initCrashlytics()
        initComponent()
    }

    private fun initComponent() {
        component = DaggerAppComponent.builder()
            .application(this)
            .build()
        component.inject(this)
    }

    private fun initCrashlytics() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }
}
