package com.creation.ultrasonic.impl.normal.thread;

import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

public class HookThreadWithThreeParam extends HookProbe{
    private static final String TAG = "HookThreadWith3Param";
    //ThreadGroup group, Runnable target, String name
    @Override
    protected MethodSign getMethodSign() {
        return MethodSign.createVoid(
                Thread.class,
                "<init>",
                new Class[] {
                        ThreadGroup.class,
                        Runnable.class,
                        String.class
                }
        );
    }


    public static Thread hook(Object object,ThreadGroup group, Runnable target,String name) {
        ThreadUtils.sThreadCount.getAndIncrement();
        android.util.Log.e(TAG,"name: "+name+" sThreadCount: "+ThreadUtils.sThreadCount.get());
        android.util.Log.e(TAG,android.util.Log.getStackTraceString(new Throwable()));
        //backup();
        return backup(object,group,target,name);
    }

    public static Thread backup(Object object,ThreadGroup group, Runnable target,String name) {
        android.util.Log.e(TAG,"threadName2222: ");
        return null;
    }
}

