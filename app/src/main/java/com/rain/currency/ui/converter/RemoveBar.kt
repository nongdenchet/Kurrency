package com.rain.currency.ui.converter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.rain.currency.R
import com.rain.currency.utils.getOverlayType
import com.rain.currency.utils.getScreenSize

@SuppressLint("InflateParams")
class RemoveBar(private val context: Context) {
    private val resources = context.resources
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val removeBar: FrameLayout = LayoutInflater.from(context).inflate(R.layout.remove_bar, null) as FrameLayout
    val height = resources.getDimensionPixelSize(R.dimen.remove_bar_height)

    init {
        removeBar.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
    }

    fun update(x: Float, y: Float) {
        removeBar.setBackgroundColor(ContextCompat.getColor(context,
                if (y > getY() - height) R.color.red
                else R.color.light_red))
    }

    private fun layoutParams(): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams()
        params.format = PixelFormat.RGBA_8888
        params.type = getOverlayType()
        params.gravity = Gravity.TOP or Gravity.START
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.width = removeBar.layoutParams.width
        params.height = removeBar.layoutParams.height
        params.y = getY()
        params.x = 0
        return params
    }

    fun getY() = getScreenSize(windowManager).heightPixels - height

    fun attach() {
        if (!ViewCompat.isAttachedToWindow(removeBar)) {
            windowManager.addView(removeBar, layoutParams())
        }
    }

    fun detach() {
        try {
            windowManager.removeView(removeBar)
        } catch (e: IllegalArgumentException) {
            // Ignore
        }
    }
}
