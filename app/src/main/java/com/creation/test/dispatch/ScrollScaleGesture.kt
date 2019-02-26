package com.creation.test.dispatch

import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.PointF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

/**
 * 预览View，负责缩放、移动还有事件拦截相关的
 */
@SuppressLint("AppCompatCustomView")
open class ScrollScaleGesture(val view: View) {
    companion object {
        const val TAG = "PreviewScaleView"
    }

    private var gestureDetector: GestureDetector
    private val scaleGestureDetector: ScaleGestureDetector
    private val trackingAnchor = PointF()
    private var isScaling = false
    val anchor = PointF()

    lateinit var onScroll: (dx: Float, dy: Float) -> Unit
    lateinit var onScale: (scale: Float) -> Unit
    lateinit var onScaleEnd: () -> Unit
    lateinit var onFinish: () -> Unit
    lateinit var onDoubleTap: (anchor: PointF) -> Unit

    init {
        gestureDetector = GestureDetector(view.context, GestureListener())
        scaleGestureDetector = ScaleGestureDetector(view.context, ScaleListener())
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            onFinish()
        }

        // 多点触控直接拦截
        val pointerCount = event.pointerCount
        if (pointerCount > 1) {
            // 计算缩放锚点的位置
            var totalX = 0f
            var totalY = 0f
            for (i in 0 until pointerCount) {
                totalX += event.getX(i)
                totalY += event.getY(i)
            }
            trackingAnchor.set(totalX / pointerCount, totalY / pointerCount)

            scaleGestureDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event) // 为了防止缩放后位置直接突变，缩放中也继续进行滑动识别
            return true
        }

        gestureDetector.onTouchEvent(event)
        return true
    }

    inner class GestureListener: GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(event1: MotionEvent?, event2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (isScaling) {
                return true
            }
            this@ScrollScaleGesture.onScroll(distanceX, distanceY)
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            this@ScrollScaleGesture.onDoubleTap(anchor)
            // 为了解决双击后不能滑动的问题，重新创建一个
            gestureDetector = GestureDetector(view.context, this)
            return true
        }
    }

    inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            isScaling = true
            anchor.set(trackingAnchor.x - view.width / 2, trackingAnchor.y -view.height / 2)
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            isScaling = false
            this@ScrollScaleGesture.onScaleEnd()
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            onScale(detector.scaleFactor)
            return false
        }
    }
}
