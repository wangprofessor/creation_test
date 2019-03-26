package com.creation.ultrasonic.impl.hook;

public interface IMethodHooker {
    void hookMethod(MethodSign target, MethodSign source, MethodSign backup);
}
