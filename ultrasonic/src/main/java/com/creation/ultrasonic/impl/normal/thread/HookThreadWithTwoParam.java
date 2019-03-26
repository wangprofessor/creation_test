package com.creation.ultrasonic.impl.normal.thread;

import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

public class HookThreadWithTwoParam extends HookProbe{
    private static final String TAG = "HookThreadWithTwoParam";
    @Override
    protected MethodSign getMethodSign() {
        return MethodSign.createVoid(
                Thread.class,
                "<init>",
                new Class[] {
                        Runnable.class,
                        String.class
                }
        );
    }


    public static Thread hook(Object object,Runnable runnable,String threadName) {
        ThreadUtils.sThreadCount.getAndIncrement();
        android.util.Log.e(TAG,threadName+" sThreadCount: "+ThreadUtils.sThreadCount.get());
        android.util.Log.e(TAG,android.util.Log.getStackTraceString(new Throwable()));
        //backup();
        return backup(object,runnable,threadName);
    }

    public static Thread backup(Object object,Runnable runnable,String threadName) {
        android.util.Log.e(TAG,"threadName2222: "+ threadName);
        return null;
    }
}
