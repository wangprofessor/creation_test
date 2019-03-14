package com.lemon.faceu.gallery.scrollscale

import android.content.Context
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.lang.Exception

class ScrollScaleViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    companion object {
        private const val TAG = "ScrollScaleViewPager"
    }

    class ItemHolder(
            val path: String?,
            val width: Int,
            val height: Int,
            val imageOrVideo: Boolean,
            val itemView: View
    ) {
        lateinit var scrollScaleView: ScrollScaleView
    }

    lateinit var itemHolderSupplier: (container: ViewGroup, position: Int) -> ItemHolder
    lateinit var sizeSupplier: () -> Int
    lateinit var scrollScaleViewFinder: (itemView: View) -> ScrollScaleView
    lateinit var currentView: ScrollScaleView
        private set
    private lateinit var currentItemView: View

    private val scrollScaleGesture: ScrollScaleGesture = ScrollScaleGesture(this)
    private var isScrolling = false

    init {
        scrollScaleGesture.onScroll = { dx, dy ->
            if (!currentView.hasInit || currentView.dstRect.width() <= currentView.width) {
                fakeDragBy(-dx)
                currentView.onScroll(0f, dy)
            } else {
                val rect = Rect()
                currentView.getLocalVisibleRect(rect)
                if (dx > 0) {
                    if (rect.right < currentView.width) {
                        if (dx <= currentView.width - rect.right) {
                            fakeDragBy(-dx)
                            currentView.onScroll(0f, dy)
                        } else {
                            fakeDragBy(-(currentView.width - rect.right).toFloat())
                            currentView.onScroll(dx - (currentView.width - rect.right), dy)
                        }
                    } else if (rect.left > 0) {
                        fakeDragBy(-dx)
                        currentView.onScroll(0f, dy)
                    } else {
                        if (dx <= currentView.dstRect.right - currentView.width) {
                            currentView.onScroll(dx, dy)
                        } else {
                            currentView.onScroll((currentView.dstRect.right - currentView.width).toFloat(), dy)
                            fakeDragBy(-(dx - (currentView.dstRect.right - currentView.width)))
                        }
                    }
                } else {
                    if (rect.right < currentView.width) {
                        fakeDragBy(-dx)
                        currentView.onScroll(0f, dy)
                    } else if (rect.left > 0) {
                        if (-dx <= rect.left) {
                            fakeDragBy(-dx)
                            currentView.onScroll(0f, dy)
                        } else {
                            fakeDragBy(rect.left.toFloat())
                            currentView.onScroll(-(-dx - rect.left), dy)
                        }
                    } else {
                        if (-dx <= -currentView.dstRect.left) {
                            currentView.onScroll(dx, dy)
                        } else {
                            currentView.onScroll(currentView.dstRect.left.toFloat(), dy)
                            fakeDragBy(-dx - (-currentView.dstRect.left))
                        }
                    }
                }
            }
        }
        scrollScaleGesture.onScale = { scale, anchor ->
            currentView.onScale(scale, anchor)
        }
        scrollScaleGesture.onScaleEnd = {
            currentView.onScaleEnd()
        }
        scrollScaleGesture.onDoubleTap = {
            currentView.onDoubleTap(it)
        }
        scrollScaleGesture.onDown = {
            beginFakeDrag()
        }
        scrollScaleGesture.onUp = {
            if (isFakeDragging) {
                endFakeDrag()
            }
            if (!scrollScaleGesture.isFling()) {
                currentView.onFinish()
            }
        }
        scrollScaleGesture.onFling = { dx, dy ->
            currentView.onScroll(dx, dy)
        }
        scrollScaleGesture.onFlingEnd = {
            currentView.onFinish()
        }

        addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    isScrolling = false
                } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    isScrolling = true
                }
            }
            override fun onPageSelected(position: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
        })
    }

    override fun fakeDragBy(xOffset: Float) {
        if (isFakeDragging) {
            super.fakeDragBy(xOffset)
        }
    }

    fun initAdapter() {
        adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return sizeSupplier()
            }

            override fun isViewFromObject(view: View, any: Any): Boolean {
                return view == any
            }

            override fun setPrimaryItem(container: ViewGroup, position: Int, any: Any) {
                currentItemView = any as View
                currentView = scrollScaleViewFinder(currentItemView)
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val width = container.width
                val height = container.height
                val itemHolder = itemHolderSupplier(container, position)
                itemHolder.scrollScaleView = scrollScaleViewFinder(itemHolder.itemView)
                container.addView(itemHolder.itemView)
                if (itemHolder.path == null || !File(itemHolder.path).exists()) {
                    return itemHolder.itemView
                }
                if (itemHolder.width == 0 || itemHolder.height == 0) {
                    Glide.with(itemHolder.scrollScaleView)
                            .load(File(itemHolder.path))
                            .into(itemHolder.scrollScaleView)
                } else {
                    var isBig = false
                    if (itemHolder.width / itemHolder.height > 3 ||
                            itemHolder.height / itemHolder.width > 3 ||
                            itemHolder.width * itemHolder.height >= width * height * 9
                    ) {
                        isBig = true
                    }
                    var applyBig = false
                    if (itemHolder.imageOrVideo && isBig) {
                        try {
                            itemHolder.scrollScaleView.initBigBitmap(itemHolder.path, BitmapRegionDecoder.newInstance(itemHolder.path, false))
                            applyBig = true
                        } catch (e: Exception) {
                        }
                    }
                    if (!applyBig) {
                        var scale = itemHolder.width.toFloat() * itemHolder.height / (width * height)
                        scale = Math.pow(scale.toDouble(), 1.0 / 3).toFloat()
                        scale = Math.max(scale, 1f)
                        val requestOptions = RequestOptions()
                                .override((itemHolder.width / scale).toInt(), (itemHolder.height/ scale).toInt())
                                .fitCenter()
                        Glide.with(itemHolder.scrollScaleView)
                                .load(File(itemHolder.path))
                                .apply(requestOptions)
                                .into(itemHolder.scrollScaleView)
                    }
                }
                itemHolder.scrollScaleView.externalEvent = true
                return itemHolder.itemView
            }

            override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
                val itemView = any as View
                container.removeView(itemView)
                val scrollScaleView = scrollScaleViewFinder(itemView)
                scrollScaleView.release()
            }

            override fun getItemPosition(any: Any): Int {
                return PagerAdapter.POSITION_NONE
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) {
            return false
        }
        if (sizeSupplier() == 0) {
            return super.onInterceptTouchEvent(ev)
        }
        if (isScrolling) {
            super.onInterceptTouchEvent(ev)
        }
        return true
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) {
            return false
        }
        if (sizeSupplier() == 0) {
            return super.onTouchEvent(ev)
        }
        if (isScrolling) {
            super.onTouchEvent(ev)
            return true
        }
        scrollScaleGesture.onTouchEvent(ev)
        currentItemView.dispatchTouchEvent(ev)
        return true
    }
}