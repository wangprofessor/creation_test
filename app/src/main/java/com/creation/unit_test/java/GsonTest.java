package com.creation.unit_test.java;

import com.google.gson.Gson;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class GsonTest {
    public static void main(String[] args) {
        System.out.println(new Gson().toJson(new A()));

        Class clazz = byte[].class;
        Constructor[] constructors = clazz.getConstructors();
        for (Constructor declaredConstructor : constructors) {
            System.out.println(declaredConstructor.toString());
        }
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            System.out.println(method);
        }

    }

    private static class A {
        public String update_time = System.currentTimeMillis() + "";
    }
}