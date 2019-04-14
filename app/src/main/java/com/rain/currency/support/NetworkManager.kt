package com.rain.currency.support

import android.content.Context
import android.net.ConnectivityManager

class NetworkManager(context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun isNetworkAvailable(): Boolean {
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}
