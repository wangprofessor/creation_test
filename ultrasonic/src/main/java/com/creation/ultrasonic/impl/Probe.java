package com.creation.ultrasonic.impl;

import com.creation.ultrasonic.IProbe;

public abstract class Probe implements IProbe {
    private boolean mIsStart = false;
    private boolean mIsStarted = false;
    private boolean mIsInit = false;

    @Override
    public final boolean isStart() {
        return mIsStart;
    }

    @Override
    public final boolean isStarted() {
        return mIsStarted;
    }

    @Override
    public final void start() {
        if (mIsStart) {
            return;
        }

        if (!mIsStarted) {
            init();
        }
        startInner();

        mIsStart = true;
        if (!mIsStarted) {
            mIsStarted = true;
        }
    }

    @Override
    public final void stop() {
        if (!mIsStart) {
            return;
        }

        stopInner();

        mIsStart = false;
    }

    @Override
    public final void init() {
        if (mIsInit) {
            return;
        }

        initInner();

        mIsInit = true;
    }

    protected abstract void startInner();
    protected abstract void stopInner();
    protected abstract void initInner();
}
