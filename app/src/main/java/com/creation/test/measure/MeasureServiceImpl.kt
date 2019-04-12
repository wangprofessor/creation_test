package com.creation.test.measure

import android.app.Activity
import android.graphics.PixelFormat
import android.graphics.Point
import android.util.Log
import android.view.View
import android.view.WindowManager

class MeasureServiceImpl: IMeasureService {
    private val TAG = "MeasureServiceImpl"

    private val immersiveSize = Point()
    private val fullScreenSize = Point()
    private val normalSize = Point()
    private var isInit = false

    override fun init(activity: Activity, finish: () -> Unit) {
        var count = 0
        val checkFinish = {
            count++
            if (count == 3) {
                isInit = true
                finish()
            }
        }

        initSizeByWindowMeasureView(activity, {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }) { width, height ->
            immersiveSize.set(width, height)
            Log.e(TAG, "immersive size:$immersiveSize")
            checkFinish()
        }

        initSizeByWindowMeasureView(activity, {
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }) { width, height ->
            fullScreenSize.set(width, height)
            Log.e(TAG, "fullScreen size:$fullScreenSize")
            checkFinish()
        }

        initSizeByWindowMeasureView(activity, {
            0
        }) { width, height ->
            normalSize.set(width, height)
            Log.e(TAG, "normal size:$normalSize")
            checkFinish()
        }
    }

    override fun isInit(): Boolean {
        return isInit
    }

    override fun getScreenWidth(): Int {
        return immersiveSize.x
    }

    override fun getScreenHeight(): Int {
        return immersiveSize.y
    }

    override fun isForbidFullScreen(): Boolean {
        return immersiveSize.y != fullScreenSize.y
    }

    private fun initSizeByWindowMeasureView(activity: Activity, flagSupplier: () -> Int, measureCallback: (Int, Int) -> Unit) {
        activity.windowManager?.let {
            val measureView = WindowMeasureView(activity) { windowMeasureView, width, height ->
                it.removeViewImmediate(windowMeasureView)
                measureCallback(width, height)
            }

            measureView.systemUiVisibility = flagSupplier()

            val params = WindowManager.LayoutParams()
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            params.format = PixelFormat.TRANSLUCENT
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN

            it.addView(measureView, params)
        }
    }
}