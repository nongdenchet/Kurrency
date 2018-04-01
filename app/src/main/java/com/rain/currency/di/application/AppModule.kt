package com.rain.currency.di.application

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.rain.currency.ui.menu.MenuHandler
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @ApplicationScope
    fun provideSharePreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName, MODE_PRIVATE)
    }

    @Provides
    @ApplicationScope
    fun provideInputMethodManager(context: Context): InputMethodManager {
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    @Provides
    @ApplicationScope
    fun providePackageManager(context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @ApplicationScope
    fun provideClipboardManager(context: Context): ClipboardManager {
        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    @Provides
    @ApplicationScope
    fun provideMenuHandler(clipboardManager: ClipboardManager): MenuHandler {
        return MenuHandler(clipboardManager)
    }
}
