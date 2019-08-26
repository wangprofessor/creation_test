package com.creation.test.appbar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.creation.test.R
import com.google.android.material.appbar.AppBarLayout

class AppbarActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appbar)

        val parent = findViewById<AppBarLayout>(R.id.parent)
//        val toolbar = findViewById<CollapsingToolbarLayout>(R.id.toolbar)
    }
}