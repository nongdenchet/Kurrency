package com.rain.currency.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.rain.currency.ui.converter.ConverterService
import com.rain.currency.ui.intro.IntroActivity
import com.rain.currency.utils.hasOverlayPermission

class SettingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (hasOverlayPermission(this)) {
            startService(Intent(this, ConverterService::class.java))
            startActivity(homeIntent())
        } else {
            finish()
            startActivity(Intent(this, IntroActivity::class.java))
        }
    }

    private fun homeIntent(): Intent {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        return intent
    }
}
