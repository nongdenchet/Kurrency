package com.rain.currency

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.rain.currency.ui.ConverterService

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(this, ConverterService::class.java))
        finish()
    }
}
