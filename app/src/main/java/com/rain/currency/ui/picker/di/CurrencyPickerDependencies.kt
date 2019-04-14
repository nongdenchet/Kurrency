package com.rain.currency.ui.picker.di

import android.view.inputmethod.InputMethodManager
import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.data.mapper.CurrencyMapper

interface CurrencyPickerDependencies {
    fun currencyRepo(): CurrencyRepo
    fun currencyMapper(): CurrencyMapper
    fun inputMethodManager(): InputMethodManager
}
