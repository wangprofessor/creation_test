package com.creation.test.measure

import android.annotation.SuppressLint
import android.content.Context
import android.view.View

@SuppressLint("ViewConstructor")
class WindowMeasureView(context: Context?, private val measureCallback: (WindowMeasureView, Int, Int) -> Unit) : View(context) {
    private var once: Boolean = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (once) {
            return
        }
        once = true
        measureCallback(this, measuredWidth, measuredHeight)
    }
}