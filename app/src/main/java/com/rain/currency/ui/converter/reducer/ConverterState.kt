package com.rain.currency.ui.converter.reducer

import com.rain.currency.domain.ConverterData

data class ConverterState(val data: ConverterData? = null, val loading: Boolean = false, val expand: Boolean = true) {
    companion object {
        val INIT_STATE = ConverterState()
    }
}
