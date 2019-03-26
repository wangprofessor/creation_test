package com.creation.ultrasonic.impl.normal.thread;

import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

public class HookThreadWithNullParam extends HookProbe{
    private static final String TAG = "HookThreadWithNullParam";
    @Override
    protected MethodSign getMethodSign() {
        return MethodSign.createVoid(
                Thread.class,
                "<init>",
                null
        );
    }


    public static Thread hook(Object object) {
        ThreadUtils.sThreadCount.getAndIncrement();
        android.util.Log.e(TAG,object+" sThreadCount: "+ThreadUtils.sThreadCount.get());
        android.util.Log.e(TAG,android.util.Log.getStackTraceString(new Throwable()));
        //backup();
        return backup(object);
    }

    public static Thread backup(Object object) {
        android.util.Log.e(TAG,"threadName2222: ");
        return null;
    }
}
