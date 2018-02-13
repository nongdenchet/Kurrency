package com.rain.currency.ui

import com.rain.currency.data.model.Currency
import com.rain.currency.data.model.Exchange

data class ConverterState(val data: Data? = null, val loading: Boolean = false, val expand: Boolean = false) {
    companion object {
        val INIT_STATE = ConverterState()
    }

    class Data(val exchange: Exchange, val currency: Currency)
}
