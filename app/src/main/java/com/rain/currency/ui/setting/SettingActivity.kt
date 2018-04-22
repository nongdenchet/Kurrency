package com.rain.currency.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.rain.currency.support.AppManager
import com.rain.currency.ui.converter.ConverterService
import com.rain.currency.ui.intro.IntroActivity
import com.rain.currency.utils.hasOverlayPermission
import dagger.android.AndroidInjection
import javax.inject.Inject

class SettingActivity : Activity() {

    @Inject
    lateinit var appManager: AppManager

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (hasOverlayPermission(this)) {
            startService(Intent(this, ConverterService::class.java))
            startActivity(appManager.homeIntent())
        } else {
            finish()
            startActivity(Intent(this, IntroActivity::class.java))
        }
    }
}
