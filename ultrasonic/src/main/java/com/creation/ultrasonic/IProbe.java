package com.creation.ultrasonic;

public interface IProbe {
    boolean isStart();
    boolean isStarted();
    void start();
    void stop();
    void init();
}
