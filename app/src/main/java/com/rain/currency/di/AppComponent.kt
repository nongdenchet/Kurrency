package com.rain.currency.di

import android.content.Context
import com.rain.currency.CurrencyApp
import com.rain.currency.ui.picker.di.CurrencyPickerDependencies
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [
    AppModule::class,
    NetworkModule::class
])
interface AppComponent : CurrencyPickerDependencies {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(app: CurrencyApp)
}
