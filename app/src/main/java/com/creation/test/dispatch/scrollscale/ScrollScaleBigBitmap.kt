package com.creation.faceu.gallery.scrollscale

import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.view.View

/**
 * 管理大图的类，比如超高截图和超宽全景图
 * 一个大图必然是比屏幕要大的，那么屏幕将会无法显示整张图片的原图
 * 所以大图思路就是，在移动过程中，哪些区域在显示范围内，就动态的去decode哪个区域
 * 缩放与移动类似，同样都是会导致大图可见区域的变化，然后依据可见区域去decode
 * 分区域decode图片需要将图片划分为若干相邻的矩形区域，比如大图的大小是6 * 2 = 12个屏幕大小，那么就分为6 * 2个矩形区域
 * 为了保障效率，缩小到最小状态的大图，不采用若干bitmap拼接的方式，而是直接decode一个整体的、缩小了的大图
 */
class ScrollScaleBigBitmap(
        private val view: View,
        path: String,
        private val displaySize: Point,
        private val decoder: BitmapRegionDecoder
) {
    companion object {
        const val TAG = "ScrollScaleBigBitmap"

        // 线程池需要外部注入
        lateinit var execute: (Runnable) -> Unit
        lateinit var remove: (Runnable?) -> Unit
    }

    private val handler = Handler(Looper.getMainLooper())
    private val paint = Paint()

    val fileSize = Point() // 原图大小
    private val gridSize = Point() // 分为几乘几区域
    private val maxScaleXy = PointF() // 大图宽高与屏幕宽高的比例
    private val maxScale: Float // 大图与屏幕宽高比两者中大的那个
    private val rectArray: Array<RectF?> // 存放矩形区域的数组
    private var initBitmap: Bitmap? = null // 缩放到最小状态的大图的bitmap
    private val bitmapArray: Array<Bitmap?> // 存放矩形区域对应bitmap的数组
    private val sampleSizeArray: IntArray // 存放各个bitmap当前decode的缩放系数，用来判断摸个区域是否需要重新decode
    private val runnableArray: Array<Runnable?> // decode各个区域的runnable，以便及时的取消不必要的decode
    private var scale = 1f // 大图当前缩放比例
    private val position: Point // 大图当前位置
    private val initSampleSize: Int // 初始化decode缩放系数
    private var currentSampleSize = 0 // 当前decode缩放系数

    init {
        // 获取图片大小
        val boundsOptions = BitmapFactory.Options()
        boundsOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, boundsOptions)
        fileSize.set(boundsOptions.outWidth, boundsOptions.outHeight)

        // 初始化比例和位置
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

        // 初始化分割区域
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

        // 加载最小状态下的大图
        loadOne(RectF(0f, 0f, fileSize.x.toFloat(), fileSize.y.toFloat())) {
            initBitmap = it
            view.invalidate()
        }
    }

    /**
     * 根据可见区域decode相应的区域
     */
    fun loadVisible() {
        computeSampleSize() // 计算decode比例
        for (i in rectArray.indices) {
            val rectF = rectArray[i]!!
            val moveScaleRect = moveScaleRect(rectF) // 变换矩形
            val rectSampleSize = sampleSizeArray[i] // 上一次的decode比例
            val intersects = moveScaleRect.intersects( // 是否相交，也就是是否可见
                    0f,
                    0f,
                    displaySize.x.toFloat(),
                    displaySize.y.toFloat()
            )
            // 两次的decode比例不一样并且可见
            if (rectSampleSize != currentSampleSize && intersects) {
                if (currentSampleSize == initSampleSize) { // 等于初始比例（初始比例就是最小比例）置null，因为initBitmap里边包含了
                    bitmapArray[i] = null
                    sampleSizeArray[i] = initSampleSize
                } else {
                    val sampleSize = currentSampleSize
                    runnableArray[i]?.let {
                        remove(it) // 取消上次加载这个区域的任务
                    }
                    runnableArray[i] = loadOne(rectF) { // decode该区域
                        bitmapArray[i] = it
                        sampleSizeArray[i] = sampleSize
                        view.invalidate()
                    }
                }
            }
        }
        view.invalidate()
    }

    /**
     * 移除所有加载任务
     */
    fun release() {
        for (runnable in runnableArray) {
            remove(runnable)
        }
    }

    /**
     * 移除不可见的区域
     */
    private fun removeInvisible() {
        computeSampleSize()
        for (i in rectArray.indices) {
            val moveScaleRect = moveScaleRect(rectArray[i]!!)
            val rectSampleSize = sampleSizeArray[i]
            val intersects = moveScaleRect.intersects(0f, 0f, displaySize.x.toFloat(), displaySize.y.toFloat())
            if (rectSampleSize != initSampleSize && !intersects) {
                bitmapArray[i] = null
                sampleSizeArray[i] = initSampleSize
            }
        }
    }

    /**
     * decode一个区域
     */
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

    /**
     * 根据当前缩放比例来计算decode缩放比例
     */
    private fun computeSampleSize() {
        var sampleSize = (maxScale / scale).toInt()
        var realSampleSize = 2
        while (sampleSize / 2 >= 1) {
            sampleSize /= 2
            realSampleSize *= 2
        }
        currentSampleSize = realSampleSize
    }

    /**
     * 通过目标位置来移动和缩放
     */
    fun moveScale(dstRect: Rect) {
        val scale: Float = if (maxScaleXy.x > maxScaleXy.y) {
            dstRect.width().toFloat() / displaySize.x
        } else {
            dstRect.height().toFloat() / displaySize.y
        }
        moveScale(dstRect.left, dstRect.top, scale)
    }

    /**
     * 移动和缩放
     */
    private fun moveScale(x: Int, y: Int, scale: Float) {
        position.set(x, y)
        this.scale = scale
        removeInvisible()
    }

    private fun moveScaleRect(rectF: RectF): RectF {
        return moveScaleRect(position, scale, rectF)
    }

    /**
     * 根据缩放比例和移动位置来变换一个区域的矩形
     */
    private fun moveScaleRect(position: Point, scale: Float, rectF: RectF): RectF {
        val rectScale = scale / maxScale
        val x = position.x + rectF.left * rectScale
        val y = position.y + rectF.top * rectScale
        val width = rectF.width() * rectScale
        val height = rectF.height() * rectScale
        return RectF(x, y, x + width, y + height)
    }

    /**
     * 绘制所有可见区域
     */
    fun draw(canvas: Canvas) {
        if (currentSampleSize == initSampleSize) { // 初始decode比例（最小比例）
            if (initBitmap != null) {
                val initBitmap = initBitmap!!
                var moveScaleRect = RectF(0f, 0f, fileSize.x.toFloat(), fileSize.y.toFloat())
                moveScaleRect = moveScaleRect(moveScaleRect) // 计算移动和缩放后的区域
                val displayRect = Rect( // 为了防止有裂痕，加一减一
                        moveScaleRect.left.toInt() - 1,
                        moveScaleRect.top.toInt() - 1,
                        moveScaleRect.right.toInt() + 1,
                        moveScaleRect.bottom.toInt() + 1
                )
                canvas.drawBitmap(
                        initBitmap,
                        Rect(0, 0, initBitmap.width, initBitmap.height),
                        displayRect,
                        paint
                )
            }
        } else {
            for (i in rectArray.indices) {
                val rect = rectArray[i]!!
                val moveScaleRect = moveScaleRect(rect) // 计算移动和缩放后的区域
                val displayRect = Rect( // 为了防止有裂痕，加一减一
                        moveScaleRect.left.toInt() - 1,
                        moveScaleRect.top.toInt() - 1,
                        moveScaleRect.right.toInt() + 1,
                        moveScaleRect.bottom.toInt() + 1
                )
                val intersects = displayRect.intersects(0, 0, displaySize.x, displaySize.y) // 是否与可见区域相交
                if (intersects) {
                    val bitmap = bitmapArray[i]
                    val initBitmap = initBitmap
                    if (bitmap != null) {
                        canvas.drawBitmap(bitmap, Rect(0, 0, bitmap.width, bitmap.height), displayRect, paint)
                    } else if (initBitmap != null) { // 如果没有，那么就从初始化的那张最小的整体图中，拿出一块对应的区域来显示
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