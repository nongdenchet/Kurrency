package com.rain.currency.support

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.rain.currency.di.application.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class AppManager @Inject constructor(private val packageManager: PackageManager) {

    private fun queryHomeLaunchers(): List<ResolveInfo> {
        return packageManager.queryIntentActivities(homeIntent(), PackageManager.MATCH_DEFAULT_ONLY)
    }

    private fun homeIntent(): Intent {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        return intent
    }

    fun getHomePackages(): Set<String> {
        return HashSet(queryHomeLaunchers()
                .filter { it.activityInfo != null }
                .map { it.activityInfo.packageName })
    }

    fun getCurrentHomePackage(): String? {
        val info = packageManager.resolveActivity(homeIntent(), PackageManager.MATCH_DEFAULT_ONLY)
        return info?.activityInfo?.packageName
    }
}
