package com.creation.test.proc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.creation.test.R

class ProcActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proc)
        val textView = findViewById<TextView>(R.id.text)
        ProcUtil.getMemInfoByType(this, "wangshouchao")
        Log.e("ProcUtil", "#######")
        ProcUtil.getVersionByType(this, "wangshouchao")
        Log.e("ProcUtil", "#######")
        ProcUtil.getCpuInfoByType(this, "wangshouchao")
    }
}