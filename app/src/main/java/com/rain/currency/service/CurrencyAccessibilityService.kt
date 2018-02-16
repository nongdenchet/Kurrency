package com.rain.currency.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.rain.currency.support.AppManager
import com.rain.currency.ui.converter.ConverterService
import com.rain.currency.utils.hasOverlayPermission
import dagger.android.AndroidInjection
import javax.inject.Inject

class CurrencyAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var appManager: AppManager

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.packageName.toString() == appManager.getCurrentHomePackage()) {
                if (hasOverlayPermission(this)) {
                    startService(Intent(this, ConverterService::class.java))
                }
            }
        }
    }
}
