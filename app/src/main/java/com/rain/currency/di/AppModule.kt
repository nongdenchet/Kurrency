package com.rain.currency.di

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.view.inputmethod.InputMethodManager
import com.rain.currency.data.CurrencyModule
import com.rain.currency.support.AssetLoader
import com.rain.currency.support.NetworkManager
import com.rain.currency.ui.menu.MenuHandler
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule

@Module(includes = [
    AndroidInjectionModule::class,
    BindingModule::class,
    CurrencyModule::class
])
object AppModule {

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .build()
    }

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideSharePreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName, MODE_PRIVATE)
    }

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideInputMethodManager(context: Context): InputMethodManager {
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    @JvmStatic
    @Provides
    @ApplicationScope
    fun providePackageManager(context: Context): PackageManager {
        return context.packageManager
    }

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideClipboardManager(context: Context): ClipboardManager {
        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideMenuHandler(clipboardManager: ClipboardManager): MenuHandler {
        return MenuHandler(clipboardManager)
    }

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideAssetLoader(context: Context) = AssetLoader(context)

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideNetworkManager(context: Context) = NetworkManager(context)
}
