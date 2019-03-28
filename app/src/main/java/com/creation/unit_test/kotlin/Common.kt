package com.creation.unit_test.kotlin

object CommonTest {
    @Volatile private var enable = false
    private lateinit var a: () -> Unit
    private lateinit var b: () -> Unit

    @JvmStatic
    fun main(args: Array<String>) {

        val ob = Runnable {
            test {

            }
        }
        ob.run()
    }

    fun eanable() {
        enable = true
        //
        //
    }

    fun tryIfEnable(a: ()->Unit) {
        if (enable) {
            a()
        }
    }

    fun test(cab: () -> Unit) {
        System.out.println("a123")

        tryIfEnable {
            a.invoke()
            b.invoke()
        }

        fun1(0, Runnable {})
    }

    private fun fun1(aa: Int, runnable: Runnable) {}
}