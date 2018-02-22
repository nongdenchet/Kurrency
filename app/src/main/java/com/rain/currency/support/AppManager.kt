package com.rain.currency.support

import android.content.Intent
import android.content.pm.PackageManager
import com.rain.currency.di.application.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class AppManager @Inject constructor(private val packageManager: PackageManager) {

    fun homeIntent(): Intent {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        return intent
    }

    fun getCurrentHomePackage(): String? {
        val info = packageManager.resolveActivity(homeIntent(), PackageManager.MATCH_DEFAULT_ONLY)
        return info?.activityInfo?.packageName
    }
}
