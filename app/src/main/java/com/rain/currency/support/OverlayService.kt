package com.rain.currency.support

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.FrameLayout
import com.rain.currency.utils.getOverlayType

abstract class OverlayService : Service(), View.OnTouchListener {
    protected lateinit var windowManager: WindowManager
    protected lateinit var window: FrameLayout
    private var originalX: Float = 0F
    private var originalY: Float = 0F
    private var moving: Boolean = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    abstract fun container(window: ViewGroup): View

    protected open fun onBackPressed() = false

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        window = object : FrameLayout(this) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                return if (event.keyCode == KeyEvent.KEYCODE_BACK && onBackPressed()) {
                    true
                } else super.dispatchKeyEvent(event)
            }
        }
        window.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        window.addView(container(window))
        window.isFocusable = true

        val params = WindowManager.LayoutParams()
        params.format = PixelFormat.RGBA_8888
        params.type = getOverlayType()
        params.gravity = Gravity.TOP or Gravity.START
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.width = window.layoutParams.width
        params.height = window.layoutParams.height
        params.x = getX()
        params.y = getY()

        windowManager.addView(window, params)
    }

    open fun getX() = 0
    open fun getY() = 0

    protected fun unFocusWindow() {
        val params = window.layoutParams as WindowManager.LayoutParams
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        windowManager.updateViewLayout(window, params)
    }

    protected fun focusWindow() {
        val params = window.layoutParams as WindowManager.LayoutParams
        params.flags = params.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        windowManager.updateViewLayout(window, params)
    }

    override fun onDestroy() {
        windowManager.removeView(window)
        super.onDestroy()
    }

    protected open fun onDragStarted(x: Float, y: Float) {}
    protected open fun onDragEnded(x: Float, y: Float) {}
    protected open fun onDragMoved(x: Float, y: Float) {}

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val x = event.rawX
        val y = event.rawY

        if (event.action == ACTION_DOWN) {
            originalX = x
            originalY = y
        } else if (event.action == ACTION_MOVE) {
            if (!moving && (Math.abs(x - originalX) > 0.5 || Math.abs(y - originalY) > 0.5)) {
                onDragStarted(x, y)
                moving = true
            }

            if (!moving) {
                return false
            }

            val params = window.layoutParams as WindowManager.LayoutParams
            params.x = (x - view.width).toInt()
            params.y = (y - view.height).toInt()
            windowManager.updateViewLayout(window, params)
            onDragMoved(x, y)
        } else if (event.action == ACTION_UP) {
            onDragEnded(x, y)
            if (moving) {
                moving = false
                return true
            }
        }
        return false
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val params = window.layoutParams as WindowManager.LayoutParams
            params.x = getX()
            params.y = getY()
            windowManager.updateViewLayout(window, params)
        }
    }
}
