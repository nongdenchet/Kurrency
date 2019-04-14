package com.rain.currency.ui

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.rain.currency.utils.hasOverlayPermission
import com.rain.currency.utils.toOverlayPermission

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

private fun getSwitchId(): String {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
        return "switchWidget"
    }

    return "switch_widget"
}
