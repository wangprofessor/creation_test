package com.creation.ultrasonic.impl.normal.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtils {
    public static AtomicInteger sThreadCount = new AtomicInteger();
    public static AtomicInteger sThreadPoolCount = new AtomicInteger();
}
