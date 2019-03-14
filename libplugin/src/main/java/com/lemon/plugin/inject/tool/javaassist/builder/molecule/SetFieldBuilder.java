package com.lemon.plugin.inject.tool.javaassist.builder.molecule;

import com.lemon.java.atom.data.node.INode;
import com.lemon.java.atom.data.node.INodeMap;
import com.lemon.java.atom.data.node.object.IField;
import com.lemon.java.atom.data.node.object.IObjectNode;
import com.lemon.plugin.inject.tool.javaassist.IMethodBuilder;
import com.lemon.plugin.inject.tool.javaassist.builder.BuilderUtils;

import java.util.Map;

public class SetFieldBuilder implements IMethodBuilder {
    private static final String setChildByNameM = "setChildByName";
    private static final String childV = "child";
    private static final String fieldNameV = "fieldName";

    @Override
    public String buildMethod(INodeMap nodeMap) {
        int depth = 0;
        String buildString = BuilderUtils.println(depth, String.format("public void %s(String %s, %s %s) {", setChildByNameM, fieldNameV, BuilderUtils.ObjectC, childV));
        buildString += buildBody(nodeMap);
        buildString += BuilderUtils.println(depth, "}");
        return buildString;
    }

    @Override
    public String buildBody(INodeMap nodeMap) {
        int depth = 1;

        String buildString = BuilderUtils.printLogo(nodeMap.getClazz(), setChildByNameM, depth);
        buildString += buildBodyInner(depth, nodeMap);

        return buildString;
    }

    private String buildBodyInner(int depth, INodeMap nodeMap) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, INode> entry : nodeMap.getSubNodeMap().entrySet()) {
            IObjectNode objectNode = (IObjectNode) entry.getValue();
            IField field = objectNode.getField();
            Class<?> clazz = field.getType();
            String fieldName = field.getName();
            String className = getArrayName(field.getType());
            String name = entry.getKey();

            BuilderUtils.println(builder, depth, String.format("if (%s.equals(\"%s\")) {", fieldNameV, name));

            depth++;
            BuilderUtils.println(builder, depth, String.format("this.%s = (%s) %s;", fieldName, className, BuilderUtils.objectToPrimitive(clazz, childV)));
            depth--;

            BuilderUtils.println(builder, depth, "}");
        }
        return builder.toString();
    }

    private String getArrayName(Class<?> clazz) {
        if (clazz.isArray()) {
            Class<?> type = clazz.getComponentType();
            return getArrayName(type) + "[]";
        } else {
            return clazz.getName();
        }
    }
}
