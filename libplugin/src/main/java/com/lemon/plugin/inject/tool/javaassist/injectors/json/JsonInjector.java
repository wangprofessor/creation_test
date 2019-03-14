package com.lemon.plugin.inject.tool.javaassist.injectors.json;

import com.lemon.java.atom.data.json.JsonMolecule;
import com.lemon.plugin.inject.tool.javaassist.ClassMethodBuilder;
import com.lemon.plugin.inject.tool.javaassist.IClassMethodBuilder;
import com.lemon.plugin.inject.tool.javaassist.IInjector;
import com.lemon.plugin.inject.tool.javaassist.builder.json.JsonBuilder;

import javassist.CtClass;

public class JsonInjector implements IInjector {
    private IClassMethodBuilder mBuilder = new ClassMethodBuilder(new JsonBuilder());

    @Override
    public boolean enable(Class<?> clazz) {
        return JsonMolecule.class.isAssignableFrom(clazz);
    }

    @Override
    public void inject(Class<?> clazz, CtClass ctClass) {
        ClassMethodBuilder.injectMethod(mBuilder, clazz, ctClass);
    }
}
