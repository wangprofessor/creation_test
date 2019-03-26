package com.creation.ultrasonic.impl.normal.thread;

import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;


/**
 * @description hook thread constructor
 * @author shuliwu
 *
 */
public class HookThread extends HookProbe {
    private static final String TAG = "HookThread";
    private static int sThreadCount= 0;
    //private void init(ThreadGroup g, Runnable target, String name, long stackSize)
    @Override
    protected MethodSign getMethodSign() {
        return MethodSign.createVoid(
                Thread.class,
                "setName",
                new Class[] {
                        String.class,
                }
        );
    }


    public static void hook(Object object ,String name) {
        android.util.Log.e(TAG,"threadName333: "+name+"   sThreadCount: "+sThreadCount);
        try {
            sThreadCount++;
            //backup(object,name);
            android.util.Log.e(TAG,android.util.Log.getStackTraceString(new Throwable()));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void backup(Object object, String name) {
        android.util.Log.e(TAG,"threadName2222: ");
    }

//    public static void hook(Object object) {
//        sThreadCount++;
//        android.util.Log.e(TAG,"threadName1: "+object+"  sThreadCount: "+sThreadCount);
//        backup(object);
//    }
//
//    public static void backup(Object object) {
//
//    }
}
