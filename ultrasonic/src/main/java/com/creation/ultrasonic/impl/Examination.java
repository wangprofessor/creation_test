package com.creation.ultrasonic.impl;

import com.creation.ultrasonic.IExamination;
import com.creation.ultrasonic.IProbe;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Examination extends Probe implements IExamination {
    final ArrayList<IProbe> mProbeList = new ArrayList<>();

    @Override
    public final void addProbe(IProbe probe) {
        mProbeList.add(probe);
        if (isStart()) {
            probe.start();
        } else {
            probe.stop();
        }
    }

    @Override
    public final void addProbes(Collection<IProbe> probes) {
        for (IProbe probe : probes) {
            if (isStart()) {
                probe.start();
            } else {
                probe.stop();
            }
        }
        mProbeList.addAll(probes);
    }

    @Override
    public final void removeProbe(IProbe probe) {
        mProbeList.remove(probe);
    }

    @Override
    public final void clearProbe() {
        mProbeList.clear();
    }

    @Override
    public final ArrayList<IProbe> getProbeList() {
        return mProbeList;
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
        ArrayList<IProbe> probeList = initProbeList();
        if (probeList == null) {
            return;
        }
        addProbes(probeList);
    }

    protected abstract ArrayList<IProbe> initProbeList();
}
