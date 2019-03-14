package com.lemon.plugin.inject.tool.javaassist;

public interface IClassMethodBuilder {
    String buildMethod(Class<?> clazz);
    String buildBody(Class<?> clazz);
}
