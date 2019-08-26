package com.creation.test

import android.net.Uri
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidTest {
    @Test
    fun test() {
        val uri = Uri.Builder()
                .scheme("content")
                .authority("com.wumii.debug")
                .path("a/b")
                .fragment("123")
                .build()
        Log.e("test", uri.toString())
        while (true) {}
    }

    private val uriBuilder = Uri.Builder().scheme("content").authority("com.wumii.debug")
    private fun createUri(name: String, key: String): Uri {
        return uriBuilder.path(name).fragment(key).build()
    }
}