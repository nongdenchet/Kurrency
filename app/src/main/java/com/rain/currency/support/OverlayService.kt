package com.rain.currency.support

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.FrameLayout
import com.rain.currency.utils.getOverlayType
import com.rain.currency.utils.getScreenSize

abstract class OverlayService : Service(), View.OnTouchListener {
    private lateinit var windowManager: WindowManager
    protected lateinit var container: FrameLayout
    private var offsetX: Float = 0.toFloat()
    private var offsetY: Float = 0.toFloat()
    private var originalXPos: Int = 0
    private var originalYPos: Int = 0
    protected var moving: Boolean = false
    protected var expand: Boolean = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    abstract fun content(container: ViewGroup): View

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        container = FrameLayout(this)
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
        params.width = container.layoutParams.width
        params.height = container.layoutParams.height
        params.x = screenSize.widthPixels / 2
        params.y = screenSize.heightPixels / 2

        windowManager.addView(container, params)
    }

    override fun onDestroy() {
        windowManager.removeView(container)
        super.onDestroy()
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val x = event.rawX
        val y = event.rawY

        if (event.action == MotionEvent.ACTION_DOWN) {
            moving = false

            val location = IntArray(2)
            view.getLocationOnScreen(location)
            originalXPos = location[0]
            originalYPos = location[1]
            offsetX = originalXPos - x
            offsetY = originalYPos - y
        } else if (event.action == MotionEvent.ACTION_MOVE) {
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
        } else if (event.action == MotionEvent.ACTION_UP) {
            if (moving) {
                return true
            }
        }
        return false
    }
}
