package com.rain.currency.support

import android.content.Intent
import com.rain.currency.di.application.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class AppManager @Inject constructor() {

    fun homeIntent(): Intent {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        return intent
    }
}
