package com.rain.currency.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.rain.currency.ui.converter.ConverterService
import com.rain.currency.utils.hasOverlayPermission

class SettingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasOverlayPermission(this)) {
            startService(Intent(this, ConverterService::class.java))
        }
        finish()
    }
}
