package com.rain.currency.support

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
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

private const val MAX_CLICK_DURATION = 100L

abstract class OverlayService : Service(), View.OnTouchListener {
    protected lateinit var windowManager: WindowManager
    protected lateinit var window: FrameLayout

    private var moving: Boolean = false
    private var startClickTime = 0L

    override fun onBind(intent: Intent) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_NOT_STICKY

    abstract fun onCreateView(window: ViewGroup): View

    abstract fun onWindowCreated()

    protected open fun onBackPressed() = false

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        onWindowCreated()
        initWindow()
    }

    private fun initWindow() {
        window = object : FrameLayout(this) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                return if (event.keyCode == KeyEvent.KEYCODE_BACK && onBackPressed()) {
                    true
                } else super.dispatchKeyEvent(event)
            }
        }
        window.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        window.addView(onCreateView(window))
        window.isFocusable = true
        windowManager.addView(window, initLayoutParams())
    }

    private fun initLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams().apply {
            format = PixelFormat.RGBA_8888
            type = getOverlayType()
            gravity = Gravity.TOP or Gravity.START
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            flags = flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            flags = flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            width = window.layoutParams.width
            height = window.layoutParams.height
            getInitPosition().let {
                x = it.x
                y = it.y
            }
        }
    }


    protected open fun getInitPosition(): Point {
        return Point(0, 0)
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
            startClickTime = System.currentTimeMillis()
        } else if (event.action == ACTION_MOVE) {
            if (!moving && System.currentTimeMillis() - startClickTime > MAX_CLICK_DURATION) {
                onDragStarted(x, y)
                moving = true
            }

            if (!moving) {
                return false
            }

            updatePosition((x - view.width / 2).toInt(), (y - view.height * 1.5).toInt())
            onDragMoved(x, y)
        } else if (event.action == ACTION_UP) {
            moveViewToEdge(x)
            onDragEnded(x, y)
            if (moving) {
                moving = false
                return true
            }
        }

        return false
    }

    private fun moveViewToEdge(x: Float) {
        val screenSize = getScreenSize(windowManager)
        val params = window.layoutParams as WindowManager.LayoutParams
        updatePosition(
                if (x > screenSize.widthPixels / 2) screenSize.widthPixels else 0,
                params.y
        )
    }

    private fun updatePosition(x: Int, y: Int) {
        val params = (window.layoutParams as WindowManager.LayoutParams)
                .apply {
                    this.x = x
                    this.y = y
                }
        windowManager.updateViewLayout(window, params)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val screenSize = getScreenSize(windowManager)
        val params = window.layoutParams as WindowManager.LayoutParams
        updatePosition(
                if (params.x > screenSize.heightPixels / 2) screenSize.widthPixels else 0,
                getInitPosition().y
        )
    }
}
