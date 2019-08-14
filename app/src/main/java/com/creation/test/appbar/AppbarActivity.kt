package com.creation.test.appbar

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import com.creation.test.R

class AppbarActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appbar)

        val parent = findViewById<AppBarLayout>(R.id.parent)
//        val toolbar = findViewById<CollapsingToolbarLayout>(R.id.toolbar)
    }
}