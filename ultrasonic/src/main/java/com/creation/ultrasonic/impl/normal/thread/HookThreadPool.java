package com.creation.ultrasonic.impl.normal.thread;

import android.util.Log;

import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HookThreadPool extends HookProbe {
    private static final String TAG = "HookThreadPool";
    @Override
    protected MethodSign getMethodSign() {
        //ThreadPoolExecutor(int corePoolSize,
        //                              int maximumPoolSize,
        //                              long keepAliveTime,
        //                              TimeUnit unit,
        //                              BlockingQueue<Runnable> workQueue,
        //                              ThreadFactory threadFactory)
        return MethodSign.createVoid(
                ThreadPoolExecutor.class,
                "<init>",
                new Class<?>[]{
                        int.class,
                        int.class,
                        long.class,
                        TimeUnit.class,
                        BlockingQueue.class,
                        ThreadFactory.class
                }
                );
    }

    public static ThreadPoolExecutor hook(Object object,int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,
                                          BlockingQueue<Runnable> workQueue,ThreadFactory threadFactory) {
        ThreadUtils.sThreadPoolCount.getAndIncrement();
        Log.e(TAG,"corePoolSize: "+corePoolSize+"  maximumPoolSize: "+maximumPoolSize+" sThreadCount: "+ThreadUtils.sThreadPoolCount.get());
        Log.e(TAG, Log.getStackTraceString(new Throwable()));
        return backup(object,corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue,threadFactory);
        //return null;
    }

    public static ThreadPoolExecutor backup(Object object,int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue,ThreadFactory threadFactory) {
        return null;
    }
}
