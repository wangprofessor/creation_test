package com.lemon.faceu.gallery.scrollscale

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet

class ScrollScaleCropView(context: Context, attrs: AttributeSet?) : ScrollScaleView(context, attrs) {
    companion object {
        private const val SCALE = 0.8f
    }

    private var paint: Paint? = null

    init {
        limitSameScale = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (paint == null) {
            paint = Paint()
            val paint = paint!!
            paint.isAntiAlias = true
            paint.style = Paint.Style.STROKE
            paint.color = 0x7f000000
            paint.strokeWidth = height.toFloat() / 2
        }
        val paint = paint!!
        val radius = width * SCALE / 2
        canvas.drawCircle(width.toFloat() / 2, height.toFloat() / 2, radius + paint.strokeWidth / 2, paint)
    }

    override fun initSize() {
        val vhScale = srcRect.height().toFloat() / srcRect.width()
        val initWidth: Int
        val initHeight: Int
        if (vhScale > 1f) {
            initWidth = (width * SCALE).toInt()
            initHeight = (initWidth * vhScale).toInt()
        } else {
            initHeight = (width * SCALE).toInt()
            initWidth = (initHeight / vhScale).toInt()
        }
        val initLeft = (width - initWidth) / 2
        val initTop = (height - initHeight) / 2
        initRect.set(initLeft, initTop, initLeft + initWidth, initTop + initHeight)
        dstRect.set(initRect)

        val limitSize = (width * SCALE).toInt()
        val limitLeft = (width - limitSize) / 2
        val limitTop = (height - limitSize) / 2
        limitRect.set(limitLeft, limitTop, limitLeft + limitSize, limitTop + limitSize)

        baseScale = dstRect.width().toFloat() / srcRect.width()
        initScale = baseScale
    }

    fun createBitmap(): Bitmap {
        val size = (width * SCALE).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val path = Path()
        val radius = size / 2f
        path.addCircle(radius, radius, radius, Path.Direction.CW)
        canvas.clipPath(path)
        val dx = -(width - size) / 2f
        val dy = -(height - size) / 2f
        canvas.translate(dx, dy)
        super.draw(canvas)
        return bitmap
    }
}