package com.rain.currency.support

import android.content.Context
import android.net.ConnectivityManager
import com.rain.currency.di.application.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class NetworkManager @Inject constructor(context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun isNetworkAvailable(): Boolean {
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}
