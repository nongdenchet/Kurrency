package com.rain.currency.ui

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.collection.ArrayMap
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.rain.currency.BuildConfig
import com.rain.currency.data.api.LiveCurrency
import com.rain.currency.utils.hasOverlayPermission
import com.rain.currency.utils.toOverlayPermission
import com.squareup.moshi.Moshi
import java.net.URL

private val moshi = Moshi.Builder().build()

fun ensureOverlayPermission(activity: Activity) {
    if (!hasOverlayPermission(activity)) {
        toOverlayPermission(activity)
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.findObject(UiSelector().resourceId("android:id/${getSwitchId()}")).click()
        device.pressBack()
    }
}

fun cleanSharePrefs() {
    val context: Context = ApplicationProvider.getApplicationContext()
    context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
}

fun getMockServerPort() = URL(BuildConfig.BASE_URL).port

fun mockLiveCurrency(): String {
    val currencies = ArrayMap<String, Double>().apply {
        this["USDUSD"] = 1.0
        this["USDSGD"] = 2.0
    }
    val adapter = moshi.adapter(LiveCurrency::class.java)

    return adapter.toJson(LiveCurrency(System.currentTimeMillis(), "USD", currencies))
}

private fun getSwitchId(): String {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
        return "switchWidget"
    }

    return "switch_widget"
}
