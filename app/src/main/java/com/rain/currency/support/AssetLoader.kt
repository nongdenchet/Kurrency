package com.rain.currency.support

import android.content.Context
import com.rain.currency.di.application.ApplicationScope
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@ApplicationScope
class AssetLoader @Inject constructor(private val context: Context) {

    fun readTextFile(fileName: String): String? {
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            return String(buffer)
        } catch (e: IOException) {
            Timber.e(e)
        }

        return null
    }
}
