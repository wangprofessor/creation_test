package com.creation.ultrasonic.impl.normal.looper;

import android.os.Build;
import android.os.Looper;
import android.os.Trace;
import android.support.annotation.RequiresApi;
import android.util.Printer;

import com.creation.ultrasonic.IProbe;
import com.creation.ultrasonic.impl.Examination;
import com.creation.ultrasonic.impl.Probe;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class LooperExamination extends Examination {
    @Override
    protected ArrayList<IProbe> initProbeList() {
        ArrayList<IProbe> arrayList = new ArrayList<>();
        arrayList.add(new EnableLooperTrace());
        arrayList.add(new MessagePrint());
        return arrayList;
    }

    public static class EnableLooperTrace extends Probe {
        private static final long APP_TAG = 1L << 12;
        private static final String TRACE_TAG_NAME = "mTraceTag";

        private Field mTraceTagField;
        private long mLastTag;

        @Override
        protected void startInner() {
            try {
                if (mTraceTagField != null) {
                    mLastTag = (long) mTraceTagField.get(Looper.getMainLooper());
                    mTraceTagField.set(Looper.getMainLooper(), APP_TAG | mLastTag);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void stopInner() {
            try {
                if (mTraceTagField != null) {
                    mTraceTagField.set(Looper.getMainLooper(), mLastTag);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void initInner() {
            try {
                mTraceTagField = Looper.class.getDeclaredField(TRACE_TAG_NAME);
                mTraceTagField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public static class MessagePrint extends Probe {
        private static final String MESSAGE = "looper_message_";
        private static long sMessageSeq = 0;

        private Printer mPrinter;

        @Override
        protected void startInner() {
            Looper.getMainLooper().setMessageLogging(mPrinter);
        }

        @Override
        protected void stopInner() {
            Looper.getMainLooper().setMessageLogging(null);
        }

        @Override
        protected void initInner() {
            mPrinter = new Printer() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void println(String x) {
                    if (sMessageSeq % 2 == 0) {
                        Trace.beginSection(MESSAGE + sMessageSeq / 2);
                    } else {
                        Trace.endSection();
                    }
                    sMessageSeq++;
                }
            };
        }
    }
}
