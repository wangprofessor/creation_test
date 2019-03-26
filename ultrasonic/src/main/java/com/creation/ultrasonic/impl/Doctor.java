package com.creation.ultrasonic.impl;

import com.creation.ultrasonic.IDoctor;
import com.creation.ultrasonic.IProbe;
import com.creation.ultrasonic.impl.normal.bitmap.BitmapExamination;
import com.creation.ultrasonic.impl.normal.looper.LooperExamination;

import java.util.ArrayList;

public class Doctor extends Examination implements IDoctor {
    private String mName;

    public Doctor() {

    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public void finish() {
        stop();
        clearProbe();
    }

    @Override
    protected void startInner() {
        for (IProbe probe : mProbeList) {
            probe.start();
        }
    }

    @Override
    protected void stopInner() {
        for (IProbe probe : mProbeList) {
            probe.stop();
        }
    }

    @Override
    protected void initInner() {
        super.initInner();
        for (IProbe probe : mProbeList) {
            probe.init();
        }
    }

    @Override
    protected ArrayList<IProbe> initProbeList() {
        ArrayList<IProbe> arrayList = new ArrayList<>();

        arrayList.add(new BitmapExamination());
        arrayList.add(new LooperExamination());

        return arrayList;
    }
}
