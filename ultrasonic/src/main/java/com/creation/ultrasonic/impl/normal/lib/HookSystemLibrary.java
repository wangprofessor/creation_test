package com.creation.ultrasonic.impl.normal.lib;


//import com.creation.faceu.memory.NativeHook;
import com.creation.ultrasonic.impl.HookProbe;
import com.creation.ultrasonic.impl.hook.MethodSign;

/**
 * @author shuliwu
 */
public class HookSystemLibrary extends HookProbe {
    private static final String TAG = "HookSystemLibrary";
    private static final String SO_NAME = "marsxlog";
    @Override
    protected MethodSign getMethodSign() {
        return MethodSign.createVoid(
                System.class,
                "loadLibrary",
                new Class<?>[] {
                        String.class
                }
        );
    }

    public static void hook(String soName) {
        backup(soName);
        android.util.Log.e(TAG,"soName2222: "+soName);
        if(soName.equals(SO_NAME)) {
//            NativeHook.init("lib"+soName+".so");
            android.util.Log.e(TAG,"soName2222: "+soName);
//            NativeHook.init("libc.so");
        }
    }

    public static void backup(String soName) {
        android.util.Log.e(TAG,"soName1111: "+soName);
    }
}
