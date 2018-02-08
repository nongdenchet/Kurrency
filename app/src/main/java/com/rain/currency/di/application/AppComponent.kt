package com.rain.currency.di.application

import android.content.Context
import com.rain.currency.CurrencyApp
import com.rain.currency.di.activity.ActivityBindingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule

@ApplicationScope
@Component(modules = arrayOf(
        AndroidInjectionModule::class,
        ActivityBindingModule::class,
        AppModule::class
))
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(app: CurrencyApp)
}
