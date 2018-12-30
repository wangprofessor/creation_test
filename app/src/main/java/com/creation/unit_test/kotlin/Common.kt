package com.creation.unit_test.kotlin

object CommonTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val run: () -> Unit = {
            test()
        }
        run.invoke()
    }

    fun test() {
        System.out.println()
    }
}