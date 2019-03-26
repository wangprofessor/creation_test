package com.creation.ultrasonic.impl.normal.thread;

import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

public class HookThreadWithOneParam1 extends HookProbe{
    private static final String TAG = "HookThreadWithOneParam1";
    @Override
    protected MethodSign getMethodSign() {
        return MethodSign.createVoid(
                Thread.class,
                "<init>",
                new Class[] {
                        Runnable.class
                }
        );
    }


    public static Thread hook(Object object,Runnable runnable) {
        Thread thread = backup(object,runnable);
        ThreadUtils.sThreadCount.getAndIncrement();
        android.util.Log.e(TAG,"object: "+object +" sThreadCount: "+ThreadUtils.sThreadCount.get());
        android.util.Log.e(TAG,android.util.Log.getStackTraceString(new Throwable()));
        return thread;
    }

    public static Thread backup(Object object,Runnable runnable) {
        return null;
    }
}
