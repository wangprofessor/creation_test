package com.creation.ultrasonic.impl.normal.thread;

import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

public class HookThreadWithFourParam1 extends HookProbe{
    //Thread(ThreadGroup group, String name, int priority, boolean daemon)
    private static final String TAG = "HookThreadWithFourParam1";

    @Override
    protected MethodSign getMethodSign() {
        return MethodSign.createVoid(
                Thread.class,
                "<init>",
                new Class[] {
                        ThreadGroup.class,
                        String.class,
                        int.class,
                        boolean.class
                }
        );
    }

    public static Thread hook(Object object,ThreadGroup group, String name, int priority, boolean daemon) {
        ThreadUtils.sThreadCount.getAndIncrement();
        android.util.Log.e(TAG,"object: "+object+" sThreadCount: "+ThreadUtils.sThreadCount.get());
        android.util.Log.e(TAG,android.util.Log.getStackTraceString(new Throwable()));
        //backup();
        return backup(object,group,name,priority,daemon);
    }

    public static Thread backup(Object object,ThreadGroup group, String name, int priority, boolean daemon) {
        android.util.Log.e(TAG,"threadName2222: ");
        return null;
    }
}
