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
import com.rain.currency.R
import com.rain.currency.utils.getOverlayType
import com.rain.currency.utils.getScreenSize

abstract class OverlayService : Service(), View.OnTouchListener {
    protected lateinit var windowManager: WindowManager
    protected lateinit var window: FrameLayout
    private var offsetX: Float = 0.toFloat()
    private var offsetY: Float = 0.toFloat()
    private var originalXPos: Int = 0
    private var originalYPos: Int = 0
    protected var moving: Boolean = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    abstract fun container(window: ViewGroup): View

    protected open fun onBackPressed(): Boolean {
        return false
    }

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

        val screenSize = getScreenSize(windowManager)
        val buttonMoneySize = resources.getDimensionPixelSize(R.dimen.button_money_size)
        val params = WindowManager.LayoutParams()
        params.format = PixelFormat.RGBA_8888
        params.type = getOverlayType()
        params.gravity = Gravity.TOP or Gravity.START
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.width = window.layoutParams.width
        params.height = window.layoutParams.height
        params.x = screenSize.widthPixels - buttonMoneySize
        params.y = screenSize.heightPixels / 2 - buttonMoneySize / 2

        windowManager.addView(window, params)
    }

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
            moving = false

            val location = IntArray(2)
            view.getLocationOnScreen(location)
            originalXPos = location[0]
            originalYPos = location[1]
            offsetX = originalXPos - x
            offsetY = originalYPos - y

            onDragStarted(x, y)
        } else if (event.action == ACTION_MOVE) {
            val params = window.layoutParams as WindowManager.LayoutParams
            val newX = (offsetX + x).toInt()
            val newY = (offsetY + y).toInt()

            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false
            }

            params.x = newX
            params.y = newY
            windowManager.updateViewLayout(window, params)
            moving = true

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
            val screenSize = getScreenSize(windowManager)
            val buttonMoneySize = resources.getDimensionPixelSize(R.dimen.button_money_size)
            params.x = screenSize.widthPixels
            params.y = screenSize.heightPixels / 2 - buttonMoneySize / 2
            windowManager.updateViewLayout(window, params)
        }
    }
}
