package com.creation.unit_test.kotlin

object CommonTest {
    @JvmStatic
    fun main(args: Array<String>) {

        val ob = Runnable {
            test {

            }
        }
        ob.run()
    }

    fun test(cab: () -> Unit) {
        System.out.println("a123")
    }
}