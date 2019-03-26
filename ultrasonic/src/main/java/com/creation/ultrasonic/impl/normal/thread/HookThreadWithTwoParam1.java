package com.creation.ultrasonic.impl.normal.thread;

import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

public class HookThreadWithTwoParam1 extends HookProbe{

    private static final String TAG = "HookThreadWithTwoParam1";

    //Thread(ThreadGroup group, Runnable target)
    @Override
    protected MethodSign getMethodSign() {
        return MethodSign.createVoid(
                Thread.class,
                "<init>",
                new Class[] {
                        ThreadGroup.class,
                        String.class
                }
        );
    }


    public static Thread hook(Object object,ThreadGroup group, String threadName) {
        ThreadUtils.sThreadCount.getAndIncrement();
        android.util.Log.e(TAG,"threadName: "+threadName+" sThreadCount: "+ThreadUtils.sThreadCount.get());
        android.util.Log.e(TAG,android.util.Log.getStackTraceString(new Throwable()));
        //backup();
        return backup(object,group,threadName);
    }

    public static Thread backup(Object object,ThreadGroup group, String threadName) {
        android.util.Log.e(TAG,"threadName2222: ");
        return null;
    }

}
