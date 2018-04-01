package com.rain.currency

import com.rain.currency.di.StubNetworkModule
import com.rain.currency.di.application.DaggerAppComponent

class EspressoApp : CurrencyApp() {

    override fun initComponent() {
        component = DaggerAppComponent.builder()
                .application(this)
                .network(StubNetworkModule())
                .build()
        component.inject(this)
    }
}
