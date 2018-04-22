package com.rain.currency.di

import android.content.Context
import com.rain.currency.di.application.AppComponent
import com.rain.currency.di.application.AppModule
import com.rain.currency.di.application.ApplicationScope
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule

@ApplicationScope
@Component(modules = arrayOf(
        AndroidInjectionModule::class,
        BindingModule::class,
        AppModule::class,
        StubNetworkModule::class
))
interface StubAppComponent : AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(context: Context): Builder

        fun build(): AppComponent
    }
}
