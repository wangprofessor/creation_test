package com.lemon.plugin.inject.tool.javaassist;

import com.lemon.java.atom.data.Molecule;
import com.lemon.plugin.inject.InjectUtils;
import com.lemon.plugin.inject.tool.javaassist.injectors.json.JsonInjector;
import com.lemon.plugin.inject.tool.javaassist.injectors.molecule.MoleculeInjector;

import java.util.HashSet;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class Injector {
    private static CtClass sMoleculeClass;
    static {
        InjectUtils.println("init molecule start");
        ClassPool pool = ClassPool.getDefault();

        try {
            String className = Molecule.class.getName();
            InjectUtils.println("init class:" + className);
            sMoleculeClass = pool.getCtClass(className);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

        InjectUtils.println("init molecule finish");
    }

    private static HashSet<com.lemon.plugin.inject.tool.javaassist.IInjector> sInjectorMap = new HashSet<>();
    private static boolean injectInner(Class<?> clazz, CtClass ctClass) {
        boolean inject = false;
        for (IInjector injector : sInjectorMap) {
            if (!injector.enable(clazz)) {
                continue;
            }
            injector.inject(clazz, ctClass);
            inject = true;
        }
        return inject;
    }
    static {
        sInjectorMap.add(new JsonInjector());
        sInjectorMap.add(new MoleculeInjector());
    }

    private ClassLoader mClassLoader;

    public Injector() {
    }

    public void setClassLoader(ClassLoader classLoader) {
        mClassLoader = classLoader;
    }

    public boolean inject(String classPath, CtClass ctClass) {
        if (!ctClass.subclassOf(sMoleculeClass)) {
            return false;
        }

        String className = ctClass.getName();
        Class<?> clazz;
        try {
            clazz = Class.forName(className, true, mClassLoader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }
        InjectUtils.println("inject class:" + clazz.getName() + ", path:" + classPath);

        return injectInner(clazz, ctClass);
    }
}
