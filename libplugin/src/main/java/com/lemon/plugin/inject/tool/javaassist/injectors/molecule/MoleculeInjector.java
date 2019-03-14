package com.lemon.plugin.inject.tool.javaassist.injectors.molecule;

import com.lemon.java.atom.data.Molecule;
import com.lemon.java.atom.data.node.INodeMap;
import com.lemon.java.atom.data.node.NodesCreator;
import com.lemon.plugin.inject.InjectUtils;
import com.lemon.plugin.inject.tool.javaassist.ClassMethodBuilder;
import com.lemon.plugin.inject.tool.javaassist.IClassMethodBuilder;
import com.lemon.plugin.inject.tool.javaassist.IMethodBuilder;
import com.lemon.plugin.inject.tool.javaassist.IInjector;
import com.lemon.plugin.inject.tool.javaassist.builder.BuilderUtils;
import com.lemon.plugin.inject.tool.javaassist.builder.molecule.CreateChildBuilder;
import com.lemon.plugin.inject.tool.javaassist.builder.molecule.CreateNodesBuilder;
import com.lemon.plugin.inject.tool.javaassist.builder.molecule.GetFieldBuilder;
import com.lemon.plugin.inject.tool.javaassist.builder.molecule.MapBuilder;
import com.lemon.plugin.inject.tool.javaassist.builder.molecule.SetFieldBuilder;

import org.gradle.internal.impldep.aQute.bnd.build.Run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;

public class MoleculeInjector implements IInjector {
    @Override
    public boolean enable(Class<?> clazz) {
        return Molecule.class.isAssignableFrom(clazz);
    }

    @Override
    public void inject(Class<?> clazz, CtClass ctClass) {
        List<IClassMethodBuilder> builderList = new ArrayList<>();

        builderList.add(new CreateClassMethodBuilder());
        builderList.add(new ClassMethodBuilder(new CreateChildBuilder()));
        builderList.add(new ClassMethodBuilder(new MapBuilder()));
        builderList.add(new ClassMethodBuilder(new GetFieldBuilder()));
        builderList.add(new ClassMethodBuilder(new SetFieldBuilder()));
        builderList.add(new ClassMethodBuilder(new CreateNodesBuilder()));

        for (IClassMethodBuilder classMethodBuilder : builderList) {
            ClassMethodBuilder.injectMethod(classMethodBuilder, clazz, ctClass);
        }
    }

    private static class CreateClassMethodBuilder implements IClassMethodBuilder {
        @Override
        public String buildMethod(Class<?> clazz) {
            int depth = 0;
            String result = BuilderUtils.println(depth, String.format("public %s create() {", BuilderUtils.MoleculeC));
            result += buildBody(clazz);
            result += BuilderUtils.println(depth,"}");
            return result;
        }

        @Override
        public String buildBody(Class<?> clazz) {
            int depth = 1;

            String buildString = "{\n";
            buildString += BuilderUtils.printLogo(clazz, "create", depth);
            buildString += BuilderUtils.println(depth, String.format("return new %s();", clazz.getName()));
            buildString += "}";

            return buildString;
        }
    }
}
