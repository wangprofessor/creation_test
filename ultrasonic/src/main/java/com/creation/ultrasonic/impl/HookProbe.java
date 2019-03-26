package com.creation.ultrasonic.impl;

import com.creation.ultrasonic.impl.hook.Hooker;
import com.creation.ultrasonic.impl.hook.MethodSign;

import java.lang.reflect.Method;

public abstract class HookProbe extends Probe {
    private MethodSign mTarget;
    private MethodSign mHook;
    private MethodSign mBackup;

    @Override
    public void startInner() {
        Hooker.hook(mTarget, mHook, mBackup);
    }

    @Override
    public void stopInner() {
        Hooker.hook(mTarget, mBackup, mBackup);
    }

    @Override
    protected void initInner() {
        Class<?> clazz = getClass();
        Method[] methods = clazz.getDeclaredMethods();

        Method hook = null;
        Method backup = null;
        for (Method method : methods) {
            if (method.getName().equals("hook")) {
                hook = method;
            } else if (method.getName().equals("backup")) {
                backup = method;
            }
        }

        if (hook == null) {
            throw new RuntimeException("must has method:hook");
        }
        if (backup == null) {
            throw new RuntimeException("must has method:backup");
        }

        mTarget = getMethodSign();
        mHook = MethodSign.createByMethod(hook);
        mBackup = MethodSign.createByMethod(backup);
    }

    protected abstract MethodSign getMethodSign();
}
