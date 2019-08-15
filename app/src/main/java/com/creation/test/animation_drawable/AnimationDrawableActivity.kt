package com.creation.test.animation_drawable

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.creation.test.R


class AnimationDrawableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_drawable)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager

        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
                val imageView = FrameAnimationCompatView(this@AnimationDrawableActivity)
                imageView.setImageResource(R.drawable.ic_audio_play)
                imageView.setOnClickListener {
                    val animationDrawable = imageView.drawable as AnimationDrawable
                    animationDrawable.stop()
                    animationDrawable.start()
                }
                return object : RecyclerView.ViewHolder(imageView) {}
            }

            override fun getItemCount(): Int {
                return 30
            }

            override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            }
        }
    }
}