package com.creation.faceu.gallery.scrollscale

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView

/**
 * 预览View，负责缩放、移动还有事件拦截相关的
 */
@SuppressLint("AppCompatCustomView")
open class ScrollScaleView(context: Context, attrs: AttributeSet?) : ImageView(context, attrs) {
    companion object {
        const val TAG = "ScrollScaleView"
        const val MAX_SCALE = 4f
        const val INSIDE_SCALE = 0.8f
    }

    private val paint = Paint()
    var hasInit = false
        private set

    val srcRect = Rect()
    val initRect = Rect()
    val limitRect = Rect()
    var initScale = 1f
    var limitSameScale = true
    var allowInsideLimit = true

    val dstRect = Rect() // 目标位置，此变量非常重要，是缩放和位置的最终体现
    var baseScale = 1f

    private var scale = 1f
    private val position = PointF()
    private val scalePosition = PointF()
    private val insideLimitRect = Rect()
    private var isAnimating = false
    private var isInsideLimit = false

    // 大图相关字段
    private var bigBitmapPath: String? = null
    private var bigBitmapDecoder: BitmapRegionDecoder? = null
    private var scrollScaleBigBitmap: ScrollScaleBigBitmap? = null

    var externalEvent = false
    private val scrollScaleGesture = ScrollScaleGesture(this)

    init {
        paint.strokeWidth = 4f
        paint.color = 0xffffffff.toInt()

        scrollScaleGesture.onDown = {

        }
        scrollScaleGesture.onScroll = { dx, dy ->
            onScroll(dx, dy)
        }
        scrollScaleGesture.onScale = { scale, anchor ->
            onScale(scale, anchor)
        }
        scrollScaleGesture.onScaleEnd = {
            onScaleEnd()
        }
        scrollScaleGesture.onUp = {
            onFinish()
        }
        scrollScaleGesture.onFling = { dx, dy ->
            onScroll(dx, dy)
        }
        scrollScaleGesture.onFlingEnd = {
            onFinish()
        }
        scrollScaleGesture.onDoubleTap = {
            onDoubleTap(it)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!externalEvent) {
            if (event == null) {
                return false
            }
            scrollScaleGesture.onTouchEvent(event)
            return true
        }
        return super.onTouchEvent(event)
    }

    fun initBigBitmap(bigBitmapPath: String, bigBitmapDecoder: BitmapRegionDecoder) {
        this.bigBitmapPath = bigBitmapPath
        this.bigBitmapDecoder = bigBitmapDecoder
    }

    fun release() {
        hasInit = false
        scrollScaleBigBitmap?.release()
        bigBitmapPath = null
        bigBitmapDecoder = null
        scrollScaleBigBitmap = null
        setImageResource(0)
        setBackgroundResource(0)
    }

    fun onScroll(dx: Float, dy: Float) {
        if (!checkEvent()) {
            return
        }
        calculateForMove(dx, dy)
        invalidate()
    }

    fun onScale(scale: Float, anchor: PointF) {
        if (!checkEvent()) {
            return
        }
        var scaleFactor = scale
        if (baseScale * scaleFactor >= MAX_SCALE) {
            scaleFactor = MAX_SCALE / baseScale
        }
        calculateForScale(scaleFactor, anchor)
        invalidate()
    }

    fun onScaleEnd() {
        if (!checkEvent()) {
            return
        }
        if (isInsideLimit) {
            animateScale(initScale, 100, PointF(width / 2f, height / 2f))
        } else {
            baseScale *= scale
            scale = 1f
            position.set(scalePosition)
        }
    }

    fun onDoubleTap(anchor: PointF) {
        if (!limitSameScale) {
            return
        }

        val targetScale: Float
        val current = (baseScale * scale) / MAX_SCALE * 4
        targetScale = when {
            current >= 1 && current < 1.66f -> {
                MAX_SCALE / 2
            }
            current >= 1.66 && current < 2.66f -> {
                MAX_SCALE
            }
            else -> {
                1f
            }
        }
        animateScale(targetScale, 500L, anchor)
    }

    private fun animateScale(targetScale: Float, duration: Long, anchor: PointF) {
        if (!checkEvent()) {
            return
        }

        val animator = ValueAnimator.ofFloat(scale, targetScale / baseScale)
        animator.duration = duration
        animator.addUpdateListener {
            calculateForScale(it.animatedValue as Float, anchor)
            invalidate()
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                isAnimating = false
                baseScale *= scale
                scale = 1f
                position.set(scalePosition)
            }

            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
        })
        animator.start()
        isAnimating = true
    }

    fun onFinish() {
        scrollScaleBigBitmap?.loadVisible() // 刷新大图可见区域
    }

    private fun checkEvent(): Boolean {
        if (drawable == null && scrollScaleBigBitmap == null) {
            return false
        }
        if (isAnimating) {
            return false // 动画过程中不处理事件
        }
        return true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (drawable == null && bigBitmapPath == null) {
            super.onDraw(canvas)
            return
        }

        // 初始化在第一次draw的进行，以便拿到view的大小
        if (!hasInit) {
            setBackgroundColor(resources.getColor(android.R.color.black))

            if (drawable != null) {
                srcRect.set(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            } else {
                scrollScaleBigBitmap = ScrollScaleBigBitmap(this, bigBitmapPath!!, Point(width, height), bigBitmapDecoder!!)
                srcRect.set(0, 0, scrollScaleBigBitmap!!.fileSize.x, scrollScaleBigBitmap!!.fileSize.y)
            }

            initSize()
            val dScale = (1 - INSIDE_SCALE) / 2
            insideLimitRect.set(
                    initRect.left + (initRect.width() * dScale).toInt(),
                    initRect.top + (initRect.height() * dScale).toInt(),
                    initRect.right - (initRect.width() * dScale).toInt(),
                    initRect.bottom - (initRect.height() * dScale).toInt()
            )

            hasInit = true
        }
        if (drawable != null) {
            canvas.save()
            canvas.translate(dstRect.left.toFloat(), dstRect.top.toFloat())
            canvas.scale(baseScale * scale, baseScale * scale)
            drawable.draw(canvas)
            canvas.restore()
        } else {
            scrollScaleBigBitmap!!.moveScale(dstRect)
            scrollScaleBigBitmap!!.draw(canvas)
        }
    }

    open fun initSize() {
        val limitWidth: Int
        val limitHeight: Int
        val limitLeft: Int
        val limitTop: Int
        val scale: Float

        if (srcRect.width().toFloat() > width || srcRect.height().toFloat() > height) {
            if (srcRect.width().toFloat() / width > srcRect.height().toFloat() / height) {
                scale = width / srcRect.width().toFloat()
                limitWidth = width
                limitLeft = 0
                limitHeight = (srcRect.height() * scale).toInt()
                limitTop = (height - limitHeight) / 2
            } else {
                scale = height / srcRect.height().toFloat()
                limitHeight = height
                limitTop = 0
                limitWidth = (srcRect.width() * scale).toInt()
                limitLeft = (width - limitWidth) / 2
            }
        } else{
            scale = 1f
            limitWidth = srcRect.width()
            limitHeight = srcRect.height()
            limitLeft = (width - limitWidth) / 2
            limitTop = (height - limitHeight) / 2
        }

        initRect.set(limitLeft, limitTop, limitLeft + limitWidth, limitTop + limitHeight)
        dstRect.set(initRect)
        limitRect.set(initRect)

        baseScale = scale
        initScale = scale
    }

    // 移动处理
    private fun calculateForMove(deltaX: Float, deltaY: Float) {
        val dstScale = baseScale * scale
        val dstWidth = srcRect.width() * dstScale
        val dstHeight = srcRect.height() * dstScale
        var dstX = width / 2 - dstWidth / 2
        var dstY = height / 2 - dstHeight / 2

        position.x -= deltaX
        position.y -= deltaY
        if (limitSameScale) {
            if (dstRect.width() <= width) {
                position.x += deltaX
            } else {
                if (dstX + position.x > 0) {
                    position.x = -dstX
                }
                if (dstX + dstWidth + position.x < width) {
                    position.x = width - dstX - dstWidth
                }
            }

            if (dstRect.height() <= height) {
                position.y += deltaY
            } else {
                if (dstY + position.y > 0) {
                    position.y = -dstY
                }
                if (dstY + dstHeight + position.y < height) {
                    position.y = height - dstY - dstHeight
                }
            }
        } else {
            if (dstX + position.x > limitRect.left) {
                position.x = limitRect.left - dstX
            }
            if (dstX + dstWidth + position.x < limitRect.right) {
                position.x = limitRect.right - dstX - dstWidth
            }

            if (dstY + position.y > limitRect.top) {
                position.y = limitRect.top - dstY
            }
            if (dstY + dstHeight + position.y < limitRect.bottom) {
                position.y = limitRect.bottom - dstY - dstHeight
            }
        }

        dstX += position.x
        dstY += position.y
        dstRect.set(dstX.toInt(), dstY.toInt(), dstX.toInt() + dstWidth.toInt(), dstY.toInt() + dstHeight.toInt())
    }

    // 缩放处理
    private fun calculateForScale(scaleFactor: Float, anchor: PointF) {
        scale = scaleFactor

        val dstScale = baseScale * scale
        val dstWidth = srcRect.width() * dstScale
        val dstHeight = srcRect.height() * dstScale
        var dstX = width / 2 - dstWidth / 2
        var dstY = height / 2 - dstHeight / 2

        scalePosition.set(position)

        if (limitSameScale) {
            val widthScale = width.toFloat() / srcRect.width() / baseScale
            val heightScale = height.toFloat() / srcRect.height() / baseScale

            // 当宽度大于View时，需要额外处理position
            if (dstRect.width() > width) {
                if (widthScale == 1f) {
                    scalePosition.x = 0f
                } else {
                    if (scale < 1) {
                        // 缩小时同时缩放position
                        scalePosition.x = (scale - widthScale) * position.x / (1f - widthScale)
                    } else {
                        // 达到一定程度才开始使用锚
                        val startScale = if (baseScale * srcRect.width() > width) {
                            1f
                        } else {
                            width / baseScale / srcRect.width()
                        }
                        scalePosition.x = position.x + (position.x - anchor.x) * (scale - startScale)
                    }
                }
            } else {
                scalePosition.x = 0f
                position.x = 0f
            }
            if (dstRect.height() > height) {
                if (heightScale == 1f) {
                    scalePosition.y = 0f
                } else {
                    if (scale < 1) {
                        scalePosition.y = (scale - heightScale) * position.y / (1f - heightScale)
                    } else {
                        val startScale = if (baseScale * srcRect.height() > height) {
                            1f
                        } else {
                            height / baseScale / srcRect.height()
                        }
                        scalePosition.y = position.y + (position.y - anchor.y) * (scale - startScale)
                    }
                }
            } else {
                scalePosition.y = 0f
                position.y = 0f
            }
        } else {
            val widthScale = limitRect.width().toFloat() / srcRect.width() / baseScale
            val heightScale = limitRect.height().toFloat() / srcRect.height() / baseScale
            val maxScale = Math.max(widthScale, heightScale)
            val vhScale = srcRect.height().toFloat() / srcRect.width()

            val limitWidth = if (widthScale > heightScale) (limitRect.height() / vhScale).toInt() else limitRect.width()
            if (dstRect.width() > limitWidth) {
                if (maxScale == 1f) {
                    scalePosition.x = 0f
                } else {
                    if (scale < 1) {
                        // 缩小时同时缩放position
                        scalePosition.x = (scale - maxScale) * position.x / (1f - maxScale)
                    } else {
                        // 达到一定程度才开始使用锚
                        val startScale = if (baseScale * srcRect.width() > limitWidth) {
                            1f
                        } else {
                            limitWidth / baseScale / srcRect.width()
                        }
                        scalePosition.x = position.x + (position.x - anchor.x) * (scale - startScale)
                    }
                }
            } else {
                scalePosition.x = 0f
                position.x = 0f
            }
            val limitHeight = if (widthScale > heightScale) limitRect.height() else (limitRect.width() * vhScale).toInt()
            if (dstRect.height() > limitHeight) {
                if (maxScale == 1f) {
                    scalePosition.y = 0f
                } else {
                    if (scale < 1) {
                        scalePosition.y = (scale - maxScale) * position.y / (1f - maxScale)
                    } else {
                        val startScale = if (baseScale * srcRect.height() > limitHeight) {
                            1f
                        } else {
                            limitHeight / baseScale / srcRect.height()
                        }
                        scalePosition.y = position.y + (position.y - anchor.y) * (scale - startScale)
                    }
                }
            } else {
                scalePosition.y = 0f
                position.y = 0f
            }
        }

        dstX += scalePosition.x
        dstY += scalePosition.y
        dstRect.set(dstX.toInt() - 1, dstY.toInt() - 1, dstX.toInt() + dstWidth.toInt() + 1, dstY.toInt() + dstHeight.toInt() + 1)

        if (limitSameScale && allowInsideLimit) {
            isInsideLimit = !dstRect.contains(limitRect)
            if (!dstRect.contains(insideLimitRect)) {
                dstRect.set(insideLimitRect)
                scale = initScale * INSIDE_SCALE / baseScale
                position.set(0f, 0f)
            }
        } else {
            if (!dstRect.contains(limitRect)) {
                dstRect.set(initRect)
                scale = initScale / baseScale
                position.set(0f, 0f)
            }
        }
    }
}
