package com.creation.ultrasonic.impl.normal.thread;

import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

public class HookThreadWithOneParam extends HookProbe{
    private static final String TAG = "HookThreadWithOneParam";
    @Override
    protected MethodSign getMethodSign() {
        return MethodSign.create(
                Thread.class,
                "<init>",
                new Class[] {
                        String.class
                },
                Thread.class
        );
    }


    public static Thread hook(Object object,String threadName) {
        ThreadUtils.sThreadCount.getAndIncrement();
        android.util.Log.e(TAG,threadName+" sThreadCount: "+ThreadUtils.sThreadCount.get());
        android.util.Log.e(TAG,android.util.Log.getStackTraceString(new Throwable()));
        //backup();
        return backup(object,threadName);
    }

    public static Thread backup(Object object,String threadName) {
        android.util.Log.e(TAG,"threadName2222: "+ threadName);
        return null;
    }
}
