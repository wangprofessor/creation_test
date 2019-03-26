package com.creation.ultrasonic.impl.hook;

import com.creation.ultrasonic.impl.hook.yahfa.YahfaMethodHooker;

public class Hooker implements IMethodHooker {
    private static final Hooker sHooker = new Hooker();

    public static void hook(MethodSign target, MethodSign source, MethodSign backup) {
        sHooker.hookMethod(target, source, backup);
    }

    private final IMethodHooker methodHooker;

    private Hooker() {
        methodHooker = new YahfaMethodHooker();
    }

    @Override
    public void hookMethod(MethodSign target, MethodSign source, MethodSign backup) {
        methodHooker.hookMethod(target, source, backup);
    }
}
