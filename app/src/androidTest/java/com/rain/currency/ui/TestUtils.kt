package com.rain.currency.ui

import android.app.Activity
import android.os.Build
import androidx.test.InstrumentationRegistry
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

private fun getSwitchId(): String {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
        return "switchWidget"
    }

    return "switch_widget"
}
