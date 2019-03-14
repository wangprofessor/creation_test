package com.creation.unit_test.java;

import java.util.HashMap;
import java.util.Map;

public class DaemonThread {
    public static void main(String[] args) {
        Thread daemonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + ".isDaemon() = " + Thread.currentThread().isDaemon());
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println(Thread.currentThread().getName() + "发生了异常...");
                    } finally {
                        System.out.println(Thread.currentThread().getName() + "正在发送心跳包...");
                    }
                }
            }
        }, "daemonThread");
        daemonThread.setDaemon(true);
        daemonThread.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " end_2");

        Map<Long, String> map = new HashMap<>();
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            Long key = entry.getKey();
            String value = entry.getValue();

        }
    }
}
