package com.rain.currency

import com.rain.currency.di.DaggerStubAppComponent

class EspressoApp : CurrencyApp() {

    override fun initComponent() {
        component = DaggerStubAppComponent.builder()
                .application(this)
                .build()
        component.inject(this)
    }
}
