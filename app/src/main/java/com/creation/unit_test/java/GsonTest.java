package com.creation.unit_test.java;

import com.google.gson.Gson;

public class GsonTest {
    public static void main(String[] args) {
        System.out.println(new Gson().toJson(new A()));
    }

    private static class A {
        public String update_time = System.currentTimeMillis() + "";
    }
}