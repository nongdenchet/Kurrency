package com.rain.currency

import android.app.Application
import android.app.Service
import com.crashlytics.android.Crashlytics
import com.rain.currency.di.AppComponent
import com.rain.currency.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasServiceInjector
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

class CurrencyApp : Application(), HasServiceInjector {
    lateinit var component: AppComponent

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    override fun serviceInjector(): AndroidInjector<Service> {
        return dispatchingServiceInjector
    }

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
            Fabric.with(this, Crashlytics())
            Timber.plant(CrashlyticsTree())
        }
    }
}
