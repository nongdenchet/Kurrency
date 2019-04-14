package com.rain.currency

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class EspressoRunner : AndroidJUnitRunner() {

    @Throws(Exception::class)
    override fun newApplication(classLoader: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(classLoader, EspressoApp::class.java.name, context)
    }
}
