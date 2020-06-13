package com.rain.currency

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

private const val CRASHLYTICS_KEY_PRIORITY = "priority"
private const val CRASHLYTICS_KEY_TAG = "tag"
private const val CRASHLYTICS_KEY_MESSAGE = "message"

internal class CrashlyticsTree : Timber.Tree() {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }

        crashlytics.setCustomKey(CRASHLYTICS_KEY_PRIORITY, priority)
        tag?.let { crashlytics.setCustomKey(CRASHLYTICS_KEY_TAG, it) }
        crashlytics.setCustomKey(CRASHLYTICS_KEY_MESSAGE, message)

        if (t == null) {
            crashlytics.recordException(Exception(message))
        } else {
            crashlytics.recordException(t)
        }
    }
}
