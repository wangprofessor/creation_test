package com.creation.test.dispatch.scrollscale

import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View

/**
 * 管理大图的类，比如超高截图和超宽全景图
 */
class ScrollScaleBigBitmap(
        private val view: View,
        path: String,
        private val displaySize: Point,
        private val decoder: BitmapRegionDecoder
) {
    companion object {
        const val TAG = "ScrollScaleBigBitmap"

        lateinit var execute: (Runnable) -> Unit
        lateinit var remove: (Runnable?) -> Unit
    }

    private val handler = Handler(Looper.getMainLooper())
    private val paint = Paint()

    val fileSize = Point()
    private val gridSize = Point()
    private val maxScaleXy = PointF()
    private val maxScale: Float
    private val rectArray: Array<RectF?>
    private var initBitmap: Bitmap? = null
    private val bitmapArray: Array<Bitmap?>
    private val sampleSizeArray: IntArray
    private val runnableArray: Array<Runnable?>
    private var scale = 1f
    private val position: Point
    private val initPosition: Point
    private val initSampleSize: Int
    private var currentSampleSize = 0

    init {
        val boundsOptions = BitmapFactory.Options()
        boundsOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, boundsOptions)
        fileSize.set(boundsOptions.outWidth, boundsOptions.outHeight)

        maxScaleXy.set(fileSize.x.toFloat() / displaySize.x, fileSize.y.toFloat() / displaySize.y)
        position = Point()
        if (maxScaleXy.x > maxScaleXy.y) {
            maxScale = maxScaleXy.x
            position.x = 0
            position.y = (displaySize.y - (fileSize.y / maxScaleXy.x)).toInt() / 2
        } else {
            maxScale = maxScaleXy.y
            position.y = 0
            position.x = (displaySize.x - (fileSize.x / maxScaleXy.y)).toInt() / 2
        }
        initPosition = Point(position)
        gridSize.set(Math.max(maxScaleXy.x.toInt(), 1), Math.max(maxScaleXy.y.toInt(), 1))
        rectArray = arrayOfNulls(gridSize.x * gridSize.y)
        val gridWidth = fileSize.x.toFloat() / gridSize.x
        val gridHeight = fileSize.y.toFloat() / gridSize.y
        for (i in 0 until gridSize.y) {
            val y = i * gridHeight
            for (j in 0 until gridSize.x) {
                val x = j * gridWidth
                rectArray[i * gridSize.x + j] = RectF(x, y, x + gridWidth, y + gridHeight)
            }
        }

        computeSampleSize()
        initSampleSize = currentSampleSize
        bitmapArray = arrayOfNulls(gridSize.x * gridSize.y)
        sampleSizeArray = IntArray(gridSize.x * gridSize.y) {
            initSampleSize
        }
        runnableArray = arrayOfNulls(gridSize.x * gridSize.y)

        loadOne(RectF(0f, 0f, fileSize.x.toFloat(), fileSize.y.toFloat())) {
            initBitmap = it
            view.invalidate()
        }
    }

    fun loadVisible() {
        Log.e(TAG, "1")
        computeSampleSize()
        for (i in rectArray.indices) {
            val rectF = rectArray[i]!!
            val moveScaleRect = moveScaleRect(rectArray[i]!!)
            val rectSampleSize = sampleSizeArray[i]
            if (rectSampleSize != currentSampleSize && moveScaleRect.intersects(0f, 0f, displaySize.x.toFloat(), displaySize.y.toFloat())) {
                if (currentSampleSize == initSampleSize) {
                    bitmapArray[i] = null
                    sampleSizeArray[i] = initSampleSize
                    Log.e(TAG, "reset, i:$i")
                } else {
                    val sampleSize = currentSampleSize
                    runnableArray[i]?.let {
                        remove(it)
                    }
                    runnableArray[i] = loadOne(rectF) {
                        bitmapArray[i] = it
                        sampleSizeArray[i] = sampleSize
                        view.invalidate()
                        Log.e(TAG, "load, i:$i,width:${it.width},height:${it.height}")
                    }
                }
            }
        }
        view.invalidate()
    }

    fun release() {
        for (runnable in runnableArray) {
            remove(runnable)
        }
    }

    private fun removeInvisible() {
        computeSampleSize()
        for (i in rectArray.indices) {
            val moveScaleRect = moveScaleRect(rectArray[i]!!)
            val rectSampleSize = sampleSizeArray[i]
            if (rectSampleSize != initSampleSize && !moveScaleRect.intersects(0f, 0f, displaySize.x.toFloat(), displaySize.y.toFloat())) {
                bitmapArray[i] = null
                sampleSizeArray[i] = initSampleSize
                Log.e(TAG, "reset, i:$i")
            }
        }
    }

    private fun loadOne(rectF: RectF, finish: (Bitmap) -> Unit): Runnable {
        val options = BitmapFactory.Options()
        options.inSampleSize = currentSampleSize
        val rect = Rect(
                rectF.left.toInt(),
                rectF.top.toInt(),
                (rectF.left + rectF.width()).toInt(),
                (rectF.top + rectF.height()).toInt()
        )
        val runnable = Runnable {
            val bitmap = decoder.decodeRegion(rect, options)
            handler.post {
                finish(bitmap)
            }
        }
        execute(runnable)
        return runnable
    }

    private fun computeSampleSize() {
        var sampleSize = (maxScale / scale).toInt()
        var realSampleSize = 2
        while (sampleSize / 2 >= 1) {
            sampleSize /= 2
            realSampleSize *= 2
        }
        currentSampleSize = realSampleSize
    }

    fun moveScale(dstRect: Rect) {
        val scale: Float = if (maxScaleXy.x > maxScaleXy.y) {
            dstRect.width().toFloat() / displaySize.x
        } else {
            dstRect.height().toFloat() / displaySize.y
        }
        moveScale(dstRect.left, dstRect.top, scale)
    }

    private fun moveScale(x: Int, y: Int, scale: Float) {
        position.set(x, y)
        this.scale = scale
        removeInvisible()
    }

    private fun moveScaleRect(rectF: RectF): RectF {
        return moveScaleRect(position, scale, rectF)
    }

    private fun moveScaleRect(position: Point, scale: Float, rectF: RectF): RectF {
        val rectScale = scale / maxScale
        val x = position.x + rectF.left * rectScale
        val y = position.y + rectF.top * rectScale
        val width = rectF.width() * rectScale
        val height = rectF.height() * rectScale
        return RectF(x, y, x + width, y + height)
    }

    fun draw(canvas: Canvas) {
        if (currentSampleSize == initSampleSize) {
            if (initBitmap != null) {
                var moveScaleRect = RectF(0f, 0f, fileSize.x.toFloat(), fileSize.y.toFloat())
                moveScaleRect = moveScaleRect(moveScaleRect)
                val displayRect = Rect(
                        moveScaleRect.left.toInt() - 1,
                        moveScaleRect.top.toInt() - 1,
                        moveScaleRect.right.toInt() + 1,
                        moveScaleRect.bottom.toInt() + 1
                )
                canvas.drawBitmap(
                        initBitmap!!,
                        Rect(0, 0, initBitmap!!.width, initBitmap!!.height),
                        displayRect,
                        paint
                )
            }
        } else {
            for (i in rectArray.indices) {
                val rect = rectArray[i]!!
                val moveScaleRect = moveScaleRect(rect)
                val displayRect = Rect(
                        moveScaleRect.left.toInt() - 1,
                        moveScaleRect.top.toInt() - 1,
                        moveScaleRect.right.toInt() + 1,
                        moveScaleRect.bottom.toInt() + 1
                )
                if (displayRect.intersects(0, 0, displaySize.x, displaySize.y)) {
                    val bitmap = bitmapArray[i]
                    val initBitmap = initBitmap
                    if (bitmap != null) {
                        canvas.drawBitmap(bitmap, Rect(0, 0, bitmap.width, bitmap.height), displayRect, paint)
                    } else if (initBitmap != null) {
                        val imageScale = initBitmap.width.toFloat() / fileSize.x
                        val rectInit = RectF(rect.left * imageScale, rect.top * imageScale, rect.right * imageScale, rect.bottom * imageScale)
                        canvas.drawBitmap(
                                initBitmap,
                                Rect(rectInit.left.toInt(), rectInit.top.toInt(), rectInit.right.toInt(), rectInit.bottom.toInt()),
                                displayRect,
                                paint
                        )
                    }
                }
            }
        }
    }
}