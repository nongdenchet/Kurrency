package com.rain.currency.utils

import android.view.Window
import android.widget.EditText
import android.widget.Spinner
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

fun setText(editText: EditText, value: String) {
    if (editText.text.toString() != value) {
        editText.setText(value)
        editText.setSelection(value.length)
    }
}

fun getStreamSelection(spinner: Spinner): Observable<Int> {
    return RxAdapterView.itemSelections(spinner)
            .distinctUntilChanged()
}

fun getStreamText(editText: EditText, window: Window?): Observable<String> {
    return RxTextView.textChangeEvents(editText)
            .filter { window?.currentFocus == editText }
            .debounce(300, TimeUnit.MILLISECONDS)
            .map { it.text() }
            .map { it.toString() }
            .subscribeOn(AndroidSchedulers.mainThread())
}
