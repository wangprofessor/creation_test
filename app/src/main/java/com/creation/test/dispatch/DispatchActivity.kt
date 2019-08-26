package com.creation.test.dispatch

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.creation.test.R

class DispatchActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispatch)

        val child1 = findViewById<RecyclerView>(R.id.child1)
        val linearLayoutManager1 = LinearLayoutManager(this)
        linearLayoutManager1.orientation = RecyclerView.VERTICAL
        child1.layoutManager = linearLayoutManager1
        child1.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
                val view = ImageView(this@DispatchActivity)
                view.scaleType = ImageView.ScaleType.CENTER
                return object : RecyclerView.ViewHolder(view) {

                }
            }

            override fun getItemCount(): Int {
                return 3
            }

            override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
                (p0.itemView as ImageView).setImageResource(R.drawable.dispatch)
            }
        }

        val child2 = findViewById<RecyclerView>(R.id.child2)
        val linearLayoutManager2 = LinearLayoutManager(this)
        linearLayoutManager2.orientation = RecyclerView.VERTICAL
        child2.layoutManager = linearLayoutManager2
        child2.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
                val view = ImageView(this@DispatchActivity)
                view.scaleType = ImageView.ScaleType.CENTER
                return object : RecyclerView.ViewHolder(view) {

                }
            }

            override fun getItemCount(): Int {
                return 3
            }

            override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
                (p0.itemView as ImageView).setImageResource(R.drawable.dispatch)
            }
        }

        val parent = findViewById<MoveLayout>(R.id.dispatch_layout)
        parent.addUnit(object : MoveLayout.MoveUnit(child1) {
            override fun onInitMove() {
                handleRect.set(child1.left.toFloat(), child1.top.toFloat(), child1.right.toFloat(), child1.bottom.toFloat())
            }
        })
        parent.addUnit(object : MoveLayout.MoveUnit(child2) {
            override fun onInitMove() {
                handleRect.set(child2.left.toFloat(), child2.top.toFloat(), child2.right.toFloat(), child2.bottom.toFloat())
            }
        })
    }
}