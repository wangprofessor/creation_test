package com.creation.ultrasonic;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;

public class DoctorSingleton {
    private static final String TAG = "DoctorSingleton";

    private static IDoctor sDoctor;

    public static IDoctor getInstance() {
        if (sDoctor == null) {
            if (BuildConfig.DEBUG) {
                try {
                    Class<?> clazz = Class.forName("com.creation.ultrasonic.impl.Doctor");
                    Constructor constructor = clazz.getConstructor();
                    sDoctor = (IDoctor) constructor.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                sDoctor = new IdleDoctor();
            }
        }
        return sDoctor;
    }

    private DoctorSingleton() {}

    private static class IdleDoctor implements IDoctor {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public void setName(String name) {

        }

        @Override
        public void finish() {

        }

        @Override
        public void addProbe(IProbe probe) {

        }

        @Override
        public void addProbes(Collection<IProbe> probes) {

        }

        @Override
        public void removeProbe(IProbe probe) {

        }

        @Override
        public void clearProbe() {

        }

        @Override
        public ArrayList<IProbe> getProbeList() {
            return null;
        }

        @Override
        public boolean isStart() {
            return false;
        }

        @Override
        public boolean isStarted() {
            return false;
        }

        @Override
        public void start() {
            Log.i(TAG, "start() called");
        }

        @Override
        public void stop() {

        }

        @Override
        public void init() {

        }
    }
}
