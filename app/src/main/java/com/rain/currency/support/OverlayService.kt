package com.rain.currency.support

import android.app.Service
import android.content.Context
import android.content.Intent
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
import com.rain.currency.utils.getScreenSize

abstract class OverlayService : Service(), View.OnTouchListener {
    protected lateinit var windowManager: WindowManager
    protected lateinit var container: FrameLayout
    private var offsetX: Float = 0.toFloat()
    private var offsetY: Float = 0.toFloat()
    private var originalXPos: Int = 0
    private var originalYPos: Int = 0
    private var moving: Boolean = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    abstract fun content(container: ViewGroup): View

    protected open fun onBackPressed(): Boolean {
        return false
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        container = object : FrameLayout(this) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                return if (event.keyCode == KeyEvent.KEYCODE_BACK && onBackPressed()) {
                    true
                } else super.dispatchKeyEvent(event)
            }
        }
        container.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        container.addView(content(container))
        container.isFocusable = true

        val screenSize = getScreenSize(windowManager)
        val params = WindowManager.LayoutParams()
        params.format = PixelFormat.RGBA_8888
        params.type = getOverlayType()
        params.gravity = Gravity.TOP or Gravity.START
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.width = container.layoutParams.width
        params.height = container.layoutParams.height
        params.x = screenSize.widthPixels / 2
        params.y = screenSize.heightPixels / 2

        windowManager.addView(container, params)
    }

    protected fun unFocusWindow() {
        val params = container.layoutParams as WindowManager.LayoutParams
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        windowManager.updateViewLayout(container, params)
    }

    protected fun focusWindow() {
        val params = container.layoutParams as WindowManager.LayoutParams
        params.flags = params.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        windowManager.updateViewLayout(container, params)
    }

    override fun onDestroy() {
        windowManager.removeView(container)
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
            val params = container.layoutParams as WindowManager.LayoutParams
            val newX = (offsetX + x).toInt()
            val newY = (offsetY + y).toInt()

            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false
            }

            params.x = newX
            params.y = newY
            windowManager.updateViewLayout(container, params)
            moving = true

            onDragMoved(x, y)
        } else if (event.action == ACTION_UP) {
            onDragEnded(x, y)
            if (moving) {
                return true
            }
        }
        return false
    }
}
