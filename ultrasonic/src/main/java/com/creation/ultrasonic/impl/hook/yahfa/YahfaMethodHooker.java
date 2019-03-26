package com.creation.ultrasonic.impl.hook.yahfa;

import java.lang.reflect.Method;
import java.util.HashMap;

import lab.galaxy.yahfa.HookMain;

import com.creation.ultrasonic.impl.hook.IMethodHooker;
import com.creation.ultrasonic.impl.hook.MethodSign;

public class YahfaMethodHooker implements IMethodHooker {
    private static final HashMap<Class<?>, String> sClassStringMap = new HashMap<>();

    static {
        sClassStringMap.put(null, "V");
        sClassStringMap.put(boolean.class, "Z");
        sClassStringMap.put(byte.class, "B");
        sClassStringMap.put(char.class, "C");
        sClassStringMap.put(short.class, "S");
        sClassStringMap.put(int.class, "I");
        sClassStringMap.put(long.class, "J");
        sClassStringMap.put(float.class, "F");
        sClassStringMap.put(double.class, "D");
    }

    @Override
    public void hookMethod(MethodSign target, MethodSign source, MethodSign backup) {
        String signString = buildSignString(target.signClasses, target.returnClass);
        Method sourceMethod = getMethod(source);
        Method backupMethod = getMethod(backup);
        HookMain.findAndBackupAndHook(target.clazz, target.methodName, signString, sourceMethod, backupMethod);
    }

    private static Method getMethod(MethodSign methodSign) {
        if (methodSign.method != null) {
            return methodSign.method;
        }

        try {
            return methodSign.clazz.getDeclaredMethod(methodSign.methodName, methodSign.signClasses);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String buildSignString(Class<?>[] signClasses, Class<?> returnClass) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        if (signClasses != null) {
            for (Class<?> clazz : signClasses) {
                String classString = getClassString(clazz);
                builder.append(classString);
            }
        }
        builder.append(")");

        String returnString = getClassString(returnClass);
        builder.append(returnString);

        return builder.toString();
    }

    private static String getClassString(Class<?> clazz) {
        return getClassStringArray(clazz, "");
    }

    private static String getClassStringArray(Class<?> clazz, String classStrings) {
        if (clazz == null) {
            return sClassStringMap.get(null);
        }

        if (clazz.isArray()) {
            return getClassStringArray(clazz.getComponentType(), classStrings + "[");
        }
        String classString = sClassStringMap.get(clazz);
        if (classString == null) {
            String className = clazz.getName();
            className = className.replace(".", "/");
            classStrings += "L" + className + ";";
        } else {
            classStrings += classString;
        }
        return classStrings;
    }
}
