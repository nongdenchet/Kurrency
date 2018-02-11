package com.rain.currency.utils

import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
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

fun getOverlayType(): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    }
    return WindowManager.LayoutParams.TYPE_PHONE
}

fun getScreenSize(windowManager: WindowManager): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics
}

fun getStreamSelection(spinner: Spinner): Observable<Int> {
    return RxAdapterView.itemSelections(spinner)
            .distinctUntilChanged()
}

fun getStreamText(editText: EditText): Observable<String> {
    return RxTextView.textChangeEvents(editText)
            .filter { editText.isFocused }
            .debounce(300, TimeUnit.MILLISECONDS)
            .map { it.text() }
            .map { it.toString() }
            .subscribeOn(AndroidSchedulers.mainThread())
}
