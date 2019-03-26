package com.creation.ultrasonic.impl.normal.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;

import com.creation.ultrasonic.DoctorSingleton;
import com.creation.ultrasonic.IProbe;
import com.creation.ultrasonic.impl.Examination;
import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class BitmapExamination extends Examination {
    private static final String TAG = "BitmapExamination";

    private static final DecimalFormat sFormat = new DecimalFormat("#,###");
    private static long sBitmapTotalSize = 0;
    private static String sName;

    @Override
    protected ArrayList<IProbe> initProbeList() {
        sName = DoctorSingleton.getInstance().getName();

        ArrayList<IProbe> arrayList = new ArrayList<>();

        arrayList.add(new CreateBitmap_1());
        arrayList.add(new CreateBitmap_2());
        arrayList.add(new CreateBitmap_3());
        arrayList.add(new CreateBitmap_4());
        arrayList.add(new DecodeStream());
        arrayList.add(new DecodeFileDescriptor());

        return arrayList;
    }

    private static void printCreateBitmap(Bitmap bitmap, boolean decode, BitmapFactory.Options opts) {
        if (bitmap == null) {
            return;
        }
        if (opts != null && opts.inBitmap != null) {
            return;
        }

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        new Exception().printStackTrace(writer);
        String string = stringWriter.toString();

        String clazzName = findClassFromException(string, decode);

        int byteCount = bitmap.getByteCount();
        synchronized (BitmapExamination.class) {
            sBitmapTotalSize += byteCount;
        }
        String text = "size:" + sFormat.format(byteCount) +
                " width:" + bitmap.getWidth() +
                " height:" + bitmap.getHeight() +
                " total:" + sFormat.format(sBitmapTotalSize) +
                " " + clazzName;
        String head = decode ? "decode " : "create ";
        text = head + text;
        if (byteCount < 180 * 180 * 4) {
            Log.i(TAG, text);
        } else {
            Log.e(TAG, text);
        }
    }

    private static String findClassFromException(String string, boolean decode) {
        String name = BitmapExamination.class.getName();
        int index = string.lastIndexOf(name);
        if (index >= 0) {
            string = string.substring(index);
        }

        //去掉前边Bitmap中的打印
        if (decode) {
            name = Bitmap.class.getName();
        } else {
            name = BitmapFactory.class.getName();
        }

        index = string.lastIndexOf(name);
        if (index >= 0) {
            string = string.substring(index);
        }

        //去掉结尾
        index = string.indexOf("\n\t") + 2;
        if (index < 0 || index >= string.length()) {
            return null;
        }
        string = string.substring(index);

        index = string.indexOf(sName);
        if (index > 0) {
            //包含自己的类
            string = string.substring(index);
            index = string.indexOf("\n\t");
            string = string.substring(0, index);
        } else {
            //获取系统的类
            String[] strings = string.split("\n\t");
            if (strings.length == 0) {
                return null;
            }
            string = strings[0];
        }

        return string;
    }

    public static class CreateBitmap_1 extends HookProbe {
        @Override
        protected MethodSign getMethodSign() {
            return MethodSign.create(
                    Bitmap.class,
                    "createBitmap",
                    new Class<?>[] {
                            int.class,
                            int.class,
                            Bitmap.Config.class
                    },
                    Bitmap.class
            );
        }

        public static Bitmap hook(int width, int height, Bitmap.Config config) {
            Bitmap bitmap = backup(width, height, config);
            printCreateBitmap(bitmap, false, null);
            return bitmap;
        }

        public static Bitmap backup(int width, int height, Bitmap.Config config) {
            return null;
        }
    }

    public static class CreateBitmap_2 extends HookProbe {
        @Override
        protected MethodSign getMethodSign() {
            return MethodSign.create(
                    Bitmap.class,
                    "createBitmap",
                    new Class<?>[] {
                            Bitmap.class,
                            int.class,
                            int.class,
                            int.class,
                            int.class,
                            Matrix.class,
                            boolean.class
                    },
                    Bitmap.class
            );
        }

        public static Bitmap hook(Bitmap source, int x, int y, int width, int height,
                           Matrix m, boolean filter) {
            Bitmap bitmap = backup(source, x, y, width, height, m, filter);
            printCreateBitmap(bitmap, false, null);
            return bitmap;
        }

        public static Bitmap backup(Bitmap source, int x, int y, int width, int height,
                             Matrix m, boolean filter) {
            return null;
        }
    }

    public static class CreateBitmap_3 extends HookProbe {
        @Override
        protected MethodSign getMethodSign() {
            return MethodSign.create(
                    Bitmap.class,
                    "createBitmap",
                    new Class<?>[] {
                            DisplayMetrics.class,
                            int.class,
                            int.class,
                            Bitmap.Config.class
                    },
                    Bitmap.class
            );
        }

        public static Bitmap hook(DisplayMetrics display, int width, int height, Bitmap.Config config) {
            Bitmap bitmap = backup(display, width, height, config);
            printCreateBitmap(bitmap, false, null);
            return bitmap;
        }

        public static Bitmap backup(DisplayMetrics display, int width, int height, Bitmap.Config config) {
            return null;
        }
    }

    public static class CreateBitmap_4 extends HookProbe {
        @Override
        protected MethodSign getMethodSign() {
            return MethodSign.create(
                    Bitmap.class,
                    "createBitmap",
                    new Class<?>[] {
                            DisplayMetrics.class,
                            int[].class,
                            int.class,
                            int.class,
                            int.class,
                            int.class,
                            Bitmap.Config.class
                    },
                    Bitmap.class
            );
        }

        public static Bitmap hook(DisplayMetrics display, int colors[],
                           int offset, int stride, int width, int height, Bitmap.Config config) {
            Bitmap bitmap = backup(display, colors, offset, stride, width, height, config);
            printCreateBitmap(bitmap, false, null);
            return bitmap;
        }

        public static Bitmap backup(DisplayMetrics display, int colors[],
                             int offset, int stride, int width, int height, Bitmap.Config config) {
            return null;
        }
    }

    public static class DecodeStream extends HookProbe {
        @Override
        protected MethodSign getMethodSign() {
            return MethodSign.create(
                    BitmapFactory.class,
                    "decodeStream",
                    new Class<?>[] {
                            InputStream.class,
                            Rect.class,
                            BitmapFactory.Options.class
                    },
                    Bitmap.class
            );
        }

        public static Bitmap hook(InputStream is, Rect outPadding, BitmapFactory.Options opts) {
            Bitmap bitmap = backup(is, outPadding, opts);
            printCreateBitmap(bitmap, true, opts);
            return bitmap;
        }

        public static Bitmap backup(InputStream is, Rect outPadding, BitmapFactory.Options opts) {
            return null;
        }
    }

    public static class DecodeFileDescriptor extends HookProbe {
        @Override
        protected MethodSign getMethodSign() {
            return MethodSign.create(
                    BitmapFactory.class,
                    "decodeFileDescriptor",
                    new Class<?>[] {
                            FileDescriptor.class,
                            Rect.class,
                            BitmapFactory.Options.class
                    },
                    Bitmap.class
            );
        }

        public static Bitmap hook(FileDescriptor fd, Rect outPadding, BitmapFactory.Options opts) {
            Bitmap bitmap = backup(fd, outPadding, opts);
            printCreateBitmap(bitmap, true, opts);
            return bitmap;
        }

        public static Bitmap backup(FileDescriptor fd, Rect outPadding, BitmapFactory.Options opts) {
            return null;
        }
    }
}
