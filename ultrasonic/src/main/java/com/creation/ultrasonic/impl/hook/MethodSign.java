package com.creation.ultrasonic.impl.hook;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodSign {
    public static MethodSign createByMethod(Method method) {
        return new MethodSign(null, null, null, null, method);
    }

    public static MethodSign createVoid(Class<?> clazz, String methodName, Class<?>[] signClasses) {
        return new MethodSign(clazz, methodName, signClasses, null, null);
    }

    public static MethodSign createSimple(Class<?> clazz, String methodName, Class<?> returnClass) {
        return new MethodSign(clazz, methodName, null, returnClass, null);
    }

    public static MethodSign createSimpleViod(Class<?> clazz, String methodName) {
        return new MethodSign(clazz, methodName, null, null, null);
    }

    public static MethodSign create(Class<?> clazz, String methodName, Class<?>[] signClasses, Class<?> returnClass) {
        return new MethodSign(clazz, methodName, signClasses, returnClass, null);
    }

    public final Class<?> clazz;
    public final String methodName;
    public final Class<?>[] signClasses;
    public final Class<?> returnClass;

    public final Method method;

    private MethodSign(Class<?> clazz, String methodName, Class<?>[] signClasses, Class<?> returnClass, Method method) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.signClasses = signClasses;
        this.returnClass = returnClass;
        this.method = method;
    }

    @Override
    public String toString() {
        return "MethodSign{" +
                "clazz=" + clazz +
                ", methodName='" + methodName + '\'' +
                ", signClasses=" + Arrays.toString(signClasses) +
                ", returnClass=" + returnClass +
                ", method=" + method +
                '}';
    }
}
