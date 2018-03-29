package com.rain.currency.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.support.annotation.DrawableRes
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import org.reactivestreams.Publisher
import java.text.NumberFormat
import java.util.Locale
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

fun exponentialBackoff(times: Int, delay: Long): Function<in Flowable<Throwable>, out Publisher<*>> {
    return Function { error ->
        error.zipWith(Flowable.range(1, times), BiFunction<Throwable, Int, Int> { _, i -> i })
                .flatMap { Flowable.timer(delay * it, TimeUnit.SECONDS) }
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

fun loadIcon(imageView: ImageView, @DrawableRes icon: Int) {
    Glide.with(imageView.context)
            .load(icon)
            .into(imageView)
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
            .distinctUntilChanged()
            .sample(300, TimeUnit.MILLISECONDS)
            .map { it.text() }
            .map { it.toString() }
            .subscribeOn(AndroidSchedulers.mainThread())
}

fun getLocale(context: Context): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0]
    } else {
        context.resources.configuration.locale
    }
}

fun getClicks(view: View): Observable<Any> {
    return RxView.clicks(view)
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
}
