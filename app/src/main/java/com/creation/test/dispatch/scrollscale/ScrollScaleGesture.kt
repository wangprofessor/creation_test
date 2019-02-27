package com.creation.test.dispatch.scrollscale

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
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
        const val TAG = "ScrollScaleGesture"
    }

    lateinit var onScroll: (dx: Float, dy: Float) -> Unit
    lateinit var onScale: (scale: Float, anchor: PointF) -> Unit
    lateinit var onScaleEnd: () -> Unit
    lateinit var onDown: () -> Unit
    lateinit var onUp: () -> Unit
    lateinit var onFling: (dx: Float, dy: Float) -> Unit
    lateinit var onFlingEnd: () -> Unit
    lateinit var onDoubleTap: (anchor: PointF) -> Unit

    private val handler = Handler(Looper.getMainLooper())
    private var gestureDetector: GestureDetector
    private val scaleGestureDetector: ScaleGestureDetector
    private val trackingAnchor = PointF()
    private var isScaling = false
    private var isHandling = false
    private val anchor = PointF()

    private var flingRunnable: Runnable? = null

    init {
        gestureDetector = GestureDetector(view.context, GestureListener())
        scaleGestureDetector = ScaleGestureDetector(view.context, ScaleListener())
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isHandling) {
            isHandling = true
            cancelFling()
            onDown()
        }

        val finish = {
            if (event.action == MotionEvent.ACTION_UP) {
                isHandling = false
                onUp()
            }
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

            finish()
            return true
        }

        gestureDetector.onTouchEvent(event)
        finish()
        return true
    }

    fun isFling(): Boolean {
        return flingRunnable != null
    }

    private fun fling(velocityX: Float, velocityY: Float) {
        if (flingRunnable == null) {
            val vx = -velocityX
            val vy = -velocityY
            val flingStartTime = SystemClock.elapsedRealtime()
            val totalTime = Math.sqrt(0.0 + velocityX * velocityX + velocityY * velocityY).toLong() / 40
            Log.d(TAG, "vx:$vx, vy:$vy, flingStartTime:$flingStartTime, totalTime:$totalTime")
            val flingPosition = PointF()
            flingRunnable = object : Runnable {
                override fun run() {
                    var time = SystemClock.elapsedRealtime() - flingStartTime
                    var continueFling = true
                    if (time >= totalTime) {
                        time = totalTime
                        continueFling = false
                    }
                    val cvx = vx - vx / totalTime * time
                    val cvy = vy - vy / totalTime * time
                    val x = (vx + cvx) * time / 2 / 1000
                    val y = (vy + cvy) * time / 2 / 1000
                    val dx = x - flingPosition.x
                    val dy = y - flingPosition.y
                    flingPosition.set(x, y)
                    Log.d(TAG, "time:$time, dx:$dx, dy:$dy, cvx:$cvx, cvy:$cvy")
                    onFling(dx, dy)
                    if (continueFling) {
                        handler.postDelayed(this, 16)
                    } else {
                        flingRunnable = null
                        onFlingEnd()
                    }
                }
            }
            handler.post(flingRunnable)
        }
    }

    private fun cancelFling() {
        if (flingRunnable != null) {
            handler.removeCallbacks(flingRunnable)
            flingRunnable = null
        }
    }

    inner class GestureListener: GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(event1: MotionEvent?, event2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (isScaling) {
                return true
            }
            this@ScrollScaleGesture.onScroll(distanceX, distanceY)
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            fling(velocityX, velocityY)
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
            onScale(detector.scaleFactor, anchor)
            return false
        }
    }
}
