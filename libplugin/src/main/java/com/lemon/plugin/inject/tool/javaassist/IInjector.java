package com.lemon.plugin.inject.tool.javaassist;

import javassist.CtClass;

public interface IInjector {
    boolean enable(Class<?> clazz);
    void inject(Class<?> clazz, CtClass ctClass);
}
