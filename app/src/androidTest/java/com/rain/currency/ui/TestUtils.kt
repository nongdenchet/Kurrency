package com.rain.currency.ui

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import com.rain.currency.utils.hasOverlayPermission
import com.rain.currency.utils.toOverlayPermission

fun ensureOverlayPermission(activity: Activity) {
    if (!hasOverlayPermission(activity)) {
        toOverlayPermission(activity)
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.findObject(UiSelector().resourceId("android:id/switch_widget")).click()
        device.pressBack()
    }
}
