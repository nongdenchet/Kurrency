package com.rain.currency.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

fun setText(editText: EditText, value: String) {
    if (editText.text.toString() != value) {
        editText.setText(value)
        editText.setSelection(value.length)
    }
}

fun hasOverlayPermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context.applicationContext)
}

fun toOverlayPermission(context: Context) {
    if (!hasOverlayPermission(context)) {
        val intent = Intent()
        intent.action = ACTION_MANAGE_OVERLAY_PERMISSION
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }
}

fun setMoney(editText: EditText, value: String) {
    if (editText.text.toString() != value) {
        try {
            val numberFormatter = NumberFormat.getNumberInstance()
            val number = numberFormatter.parse(value)
            val result = numberFormatter.format(number)
            setText(editText, result)
        } catch (e: Exception) {
            setText(editText, "")
        }
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

fun getStreamText(editText: EditText): Observable<String> {
    return RxTextView.textChangeEvents(editText)
            .filter { editText.isFocused }
            .debounce(300, TimeUnit.MILLISECONDS)
            .map { it.text() }
            .map { it.toString() }
            .subscribeOn(AndroidSchedulers.mainThread())
}

fun getClicks(view: View): Observable<Any> {
    return RxView.clicks(view)
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
}
