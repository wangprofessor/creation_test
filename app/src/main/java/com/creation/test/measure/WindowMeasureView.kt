package com.creation.test.measure

import android.content.Context
import android.view.View

class MeasureView(context: Context?, private val measureCallback: (Int, Int) -> Unit) : View(context) {
    private var once: Boolean = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (once) {
            return
        }
        once = true
        measureCallback(width, height)
    }
}