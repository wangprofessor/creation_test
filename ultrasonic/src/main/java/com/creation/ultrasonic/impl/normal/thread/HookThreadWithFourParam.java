package com.creation.ultrasonic.impl.normal.thread;

import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

public class HookThreadWithFourParam extends HookProbe{

    private static final String TAG = "HookThreadWithFourParam";

    @Override
    protected MethodSign getMethodSign() {
        return MethodSign.createVoid(
                Thread.class,
                "<init>",
                new Class[] {
                        ThreadGroup.class,
                        Runnable.class,
                        String.class,
                        long.class
                }
        );
    }

    public static Thread hook(Object object,ThreadGroup threadGroup,Runnable runnable,String threadName,long stackSize) {
        ThreadUtils.sThreadCount.getAndIncrement();
        android.util.Log.e(TAG,threadName+" sThreadCount: "+ThreadUtils.sThreadCount.get());
        android.util.Log.e(TAG,android.util.Log.getStackTraceString(new Throwable()));
        //backup();
        return backup(object,threadGroup,runnable,threadName,stackSize);
    }

    public static Thread backup(Object object,ThreadGroup threadGroup,Runnable runnable,String threadName,long stackSize) {
        android.util.Log.e(TAG,"threadName2222: "+ threadName);
        return null;
    }


}
