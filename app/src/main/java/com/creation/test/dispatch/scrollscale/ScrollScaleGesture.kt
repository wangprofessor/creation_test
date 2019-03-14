package com.lemon.faceu.gallery.scrollscale

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
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

    /**
     * 若干回调
     */
    lateinit var onScroll: (dx: Float, dy: Float) -> Unit
    lateinit var onScale: (scale: Float, anchor: PointF) -> Unit
    lateinit var onScaleEnd: () -> Unit
    lateinit var onDown: () -> Unit
    lateinit var onUp: () -> Unit
    lateinit var onFling: (dx: Float, dy: Float) -> Unit
    lateinit var onFlingEnd: () -> Unit
    lateinit var onDoubleTap: (anchor: PointF) -> Unit

    var isScrollingOrScaling = false

    private val handler = Handler(Looper.getMainLooper())
    private var gestureDetector: GestureDetector
    private val scaleGestureDetector: ScaleGestureDetector
    private var isScaling = false
    private var isHandling = false
    private val trackingAnchor = PointF() // 记录多点的中心点
    private val anchor = PointF() // 记录开始缩放时的中心点

    private var flingRunnable: Runnable? = null // fling用的runnable，来电惯性的感觉

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
                isScrollingOrScaling = false
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
        return isScrollingOrScaling
    }

    fun isFling(): Boolean {
        return flingRunnable != null
    }

    // 将fling的速度转化为若干次的onFling滑动
    private fun fling(velocityX: Float, velocityY: Float) {
        if (flingRunnable == null) {
            val vx = -velocityX
            val vy = -velocityY
            val flingStartTime = SystemClock.elapsedRealtime()
            // fling总时间，按照加速度为-40来计算
            val totalTime = Math.sqrt(0.0 + velocityX * velocityX + velocityY * velocityY).toLong() / 40
            val flingPosition = PointF()
            flingRunnable = object : Runnable {
                override fun run() {
                    // 计算当前帧该滑动的dx和dy，通过用当前时间应该滑动的总距离减去上次的距离来计算
                    var time = SystemClock.elapsedRealtime() - flingStartTime // 当前时间
                    var continueFling = true // 是否应该继续滑动
                    if (time >= totalTime) {
                        time = totalTime
                        continueFling = false
                    }
                    val cvx = vx - vx / totalTime * time // 当前x速度
                    val cvy = vy - vy / totalTime * time // 当前y速度
                    val x = (vx + cvx) * time / 2 / 1000 // 当前总x
                    val y = (vy + cvy) * time / 2 / 1000 // 当前总y
                    val dx = x - flingPosition.x
                    val dy = y - flingPosition.y
                    flingPosition.set(x, y) // 记录该次总距离，下次计算会用到

                    onFling(dx, dy)
                    if (continueFling) {
                        handler.postDelayed(this, 16) // 每帧滑动一点点
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
            // 缩放过程中不进行滚动
            if (isScaling) {
                return true
            }
            this@ScrollScaleGesture.onScroll(distanceX, distanceY)
            isScrollingOrScaling = true
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            fling(velocityX, velocityY)
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            this@ScrollScaleGesture.onDoubleTap(anchor)
            return true
        }
    }

    inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            isScaling = true
            // 缩放开始的时候记录多点的中心点
            anchor.set(trackingAnchor.x - view.width / 2, trackingAnchor.y -view.height / 2)
            isScrollingOrScaling = true
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
