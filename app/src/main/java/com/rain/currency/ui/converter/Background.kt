package com.rain.currency.ui.converter

import android.content.Context
import android.graphics.PixelFormat
import android.support.v4.view.ViewCompat
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.rain.currency.utils.getOverlayType

class Background(context: Context) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val background = FrameLayout(context)

    private fun getParams(): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams()
        params.format = PixelFormat.RGBA_8888
        params.type = getOverlayType()
        params.gravity = Gravity.TOP or Gravity.START
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.width = 0
        params.height = 0
        return params
    }

    fun setOnClickListener(listener: () -> Unit) {
        background.setOnClickListener { listener.invoke() }
    }

    fun deactivateBackground() {
        val params = background.layoutParams as WindowManager.LayoutParams
        params.width = 0
        params.height = 0
        windowManager.updateViewLayout(background, params)
    }

    fun activateBackground() {
        val params = background.layoutParams as WindowManager.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        windowManager.updateViewLayout(background, params)
    }

    fun attach() {
        if (!ViewCompat.isAttachedToWindow(background)) {
            windowManager.addView(background, getParams())
        }
    }

    fun detach() {
        try {
            windowManager.removeView(background)
        } catch (e: IllegalArgumentException) {
            // Ignore
        }
    }
}
