package com.creation.test.measure

import android.app.Activity

interface IMeasureService {
    fun init(activity: Activity, finish: () -> Unit)
    fun isInit(): Boolean
    fun getScreenWidth(): Int
    fun getScreenHeight(): Int
    fun isForbidFullScreen(): Boolean
}