package com.lemon.plugin.inject.tool.javaassist;

import com.lemon.java.atom.data.node.INodeMap;
import com.lemon.java.atom.data.node.NodesCreator;

import javassist.CtClass;
import javassist.CtMethod;

public class ClassMethodBuilder implements IClassMethodBuilder {
    public static void injectMethod(IClassMethodBuilder builder, Class<?> clazz, CtClass ctClass) {
        try {
            String methodString = builder.buildMethod(clazz);
//                InjectUtils.println(src);
            CtMethod method = CtMethod.make(methodString, ctClass);
            try {
                ctClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
            } catch (Exception ignore) {
                // method does't exist
                ctClass.addMethod(method);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final IMethodBuilder methodBuilder;

    public ClassMethodBuilder(IMethodBuilder methodBuilder) {
        this.methodBuilder = methodBuilder;
    }

    @Override
    public String buildMethod(Class<?> clazz) {
        INodeMap nodeMap = (INodeMap) NodesCreator.createNodes(clazz);
        return methodBuilder.buildMethod(nodeMap);
    }

    @Override
    public String buildBody(Class<?> clazz) {
        INodeMap nodeMap = (INodeMap) NodesCreator.createNodes(clazz);
        return "{\n" + methodBuilder.buildBody(nodeMap) + "}";
    }
}
