package com.creation.ultrasonic.impl.normal.thread;

import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

public class HookThreadStart extends HookProbe{

    private static final String TAG = "HookThreadStart";

    @Override
    protected MethodSign getMethodSign() {
        return MethodSign.createVoid(
                Thread.class,
            "start",
                new Class[]{});
    }

    public static void hook(Object object) {
        android.util.Log.e(TAG,"object: "+object);
        android.util.Log.e(TAG,android.util.Log.getStackTraceString(new Throwable()));
    }

    public static void backup(Object object) {

    }
}
