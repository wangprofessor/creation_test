package com.creation.test.memory

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.creation.test.R

class MemoryActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)
        val parent = findViewById<FrameLayout>(R.id.parent)
        val button = findViewById<Button>(R.id.button)
        var count = 0
        val resArray = intArrayOf(
                R.drawable.memory_image,
                R.drawable.memory_image2,
                R.drawable.memory_image3
        )
        button.setOnClickListener {
            val h = count % 3
            val v = count / 3
            val res = resArray[h]

            val imageView = ImageView(this)
            val requestOptions = RequestOptions()
                    .override(400, 400)
                    .centerCrop()
            Glide.with(this).load(res).apply(requestOptions).into(imageView)

            val layoutParams = FrameLayout.LayoutParams(400, 400)
            layoutParams.leftMargin = h * 400
            layoutParams.topMargin = v * 400
            parent.addView(imageView, 0, layoutParams)

            count++
        }
    }
}