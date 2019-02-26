package com.creation.test.dispatch

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

class MoveLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    companion object {
        private const val TAG = "MoveLayout"

        private const val HANDLE = 0
        private const val NOT_HANDLE = 1
        private const val MULTI_HANDLE = 2
    }

    abstract class MoveUnit(val view: View, val h: Boolean = true, val v: Boolean = true) {
        var isInit = false
        var isHandled = false
        var isHandling = false
        val handleRect: RectF = RectF()
        var lastEvent: MotionEvent? = null

        lateinit var downPoint: PointF

        abstract fun onInitMove()
    }

    private val moveUnitList: MutableList<MoveUnit> = mutableListOf()
    private val downPoint: PointF = PointF()
    private var handled = false

    private val gestureDetector = GestureDetector(context, object: GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            return true // 返回true表示处理，这样gestureDetector的onTouchEvent才会返回true
        }
    })

    init {

    }

    fun addUnit(moveUnit: MoveUnit) {
        moveUnit.downPoint = downPoint
        moveUnitList.add(moveUnit)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) {
            return false
        }

        val handle = handleEvent(ev) == HANDLE
        if (handle) {
            Log.d(TAG, "intercept")
        }
        return handle
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        val handle = handleEvent(event) == HANDLE
        if (handle) {
            for (dispatchUnit in moveUnitList) {
                if (!dispatchUnit.isInit) {
                    dispatchUnit.isInit = true
                    dispatchUnit.onInitMove()
                }

                if (dispatchUnit.handleRect.contains(event.x, event.y)) {
                    dispatchUnit.isHandling = true

                    if (!dispatchUnit.isHandled) {
                        dispatchUnit.isHandled = true
                        val ev = MotionEvent.obtain(event)
                        ev.action = MotionEvent.ACTION_DOWN
                        dispatchUnit.view.onTouchEvent(ev)
                        Log.d(TAG, "dispatch down ${moveUnitList.indexOf(dispatchUnit)} x：${ev.x}， y:${ev.y}")
                    }

                    dispatchUnit.lastEvent = event
                    dispatchUnit.view.onTouchEvent(event)
//                    Log.d(TAG, "dispatch ${moveUnitList.indexOf(dispatchUnit)} x：${event.x}， y:${event.y}")
                } else {
                    if (dispatchUnit.isHandling) {
                        dispatchUnit.isHandling = false

                        val offsetX = if (event.x < dispatchUnit.handleRect.left) {
                            dispatchUnit.handleRect.left - event.x
                        } else {
                            dispatchUnit.handleRect.right - event.x
                        }
                        val offsetY = if (event.y < dispatchUnit.handleRect.top) {
                            dispatchUnit.handleRect.top - event.y
                        } else {
                            dispatchUnit.handleRect.bottom - event.y
                        }

                        val ev = MotionEvent.obtain(event)
                        ev.offsetLocation(offsetX, offsetY)
                        dispatchUnit.lastEvent = ev
                        dispatchUnit.view.onTouchEvent(ev)
                        Log.d(TAG, "dispatch too large ${moveUnitList.indexOf(dispatchUnit)} x：${ev.x}， y:${ev.y}")
                    }
                }
            }
        }
        return true
    }

    private fun handleEvent(ev: MotionEvent): Int {
        if (ev.pointerCount > 1) {
            var multi = false
            for (dispatchUnit in moveUnitList) {
                if (dispatchUnit.isHandling) {
                    dispatchUnit.view.onTouchEvent(ev)
                    multi = true
                }
            }
            Log.d(TAG, ">>>>multi")
            return if (multi) MULTI_HANDLE else NOT_HANDLE
        }

        if (!handled && (ev.action == MotionEvent.ACTION_DOWN || ev.action == MotionEvent.ACTION_MOVE)) {
            Log.d(TAG, ">>>>start handle")
            handled = true
            downPoint.set(ev.x, ev.y)
            for (dispatchUnit in moveUnitList) {
                dispatchUnit.isInit = false
                dispatchUnit.isHandled = false
                dispatchUnit.isHandling = false
                dispatchUnit.handleRect.setEmpty()
                dispatchUnit.lastEvent = null
            }
            if (ev.action == MotionEvent.ACTION_MOVE) {
                val event = MotionEvent.obtain(ev)
                event.action = MotionEvent.ACTION_DOWN
                gestureDetector.onTouchEvent(event)
                Log.d(TAG, ">>>>add down")
            }
        } else if (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_CANCEL) {
            handled = false
            for (dispatchUnit in moveUnitList) {
                if (dispatchUnit.lastEvent != null) {
                    val event = MotionEvent.obtain(dispatchUnit.lastEvent)
                    if (dispatchUnit.isHandling) {
                        event.action = MotionEvent.ACTION_UP
                    } else {
                        event.action = MotionEvent.ACTION_CANCEL
                    }
                    dispatchUnit.view.onTouchEvent(event)
                    Log.d(TAG, "dispatch up ${moveUnitList.indexOf(dispatchUnit)} x：${event.x}， y:${event.y}")
                }
            }
            Log.d(TAG, "<<<<end handle")
            return NOT_HANDLE
        }

        return if (gestureDetector.onTouchEvent(ev)) HANDLE else NOT_HANDLE
    }
}