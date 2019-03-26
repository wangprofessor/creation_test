package com.creation.ultrasonic;

import java.util.ArrayList;
import java.util.Collection;

public interface IExamination extends IProbe {
    void addProbe(IProbe probe);
    void addProbes(Collection<IProbe> probes);
    void removeProbe(IProbe probe);
    void clearProbe();
    ArrayList<IProbe> getProbeList();
}
