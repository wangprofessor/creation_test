package com.lemon.plugin.inject.tool.javaassist.builder.molecule;

import com.lemon.java.atom.data.node.INode;
import com.lemon.java.atom.data.node.INodeMap;
import com.lemon.java.atom.data.node.object.IField;
import com.lemon.java.atom.data.node.object.IObjectNode;
import com.lemon.plugin.inject.tool.javaassist.IMethodBuilder;
import com.lemon.plugin.inject.tool.javaassist.builder.BuilderUtils;

import java.util.Map;

public class GetFieldBuilder implements IMethodBuilder {
    private static final String childV = "child";
    private static final String nameV = "name";
    private static final String getChildByNameM = "getChildByName";

    @Override
    public String buildMethod(INodeMap nodeMap) {
        int depth = 0;
        String buildString = BuilderUtils.println(depth, String.format("public %s %s(String %s) {", BuilderUtils.ObjectC, getChildByNameM, nameV));
        buildString += buildBody(nodeMap);
        buildString += BuilderUtils.println(depth, "}");
        return buildString;
    }

    @Override
    public String buildBody(INodeMap nodeMap) {
        int depth = 1;

        String buildString = BuilderUtils.printLogo(nodeMap.getClazz(), getChildByNameM, depth);

        buildString += BuilderUtils.println(depth, String.format("%s %s = null;", BuilderUtils.ObjectC, childV));
        buildString += buildBodyInner(depth, nodeMap);
        buildString += BuilderUtils.println(depth, String.format("return %s;", childV));

        return buildString;
    }

    private String buildBodyInner(int depth, INodeMap nodeMap) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, INode> entry : nodeMap.getSubNodeMap().entrySet()) {
            IObjectNode objectNode = (IObjectNode) entry.getValue();
            IField field = objectNode.getField();
            String fieldName = field.getName();
            String name = entry.getKey();
            Class<?> clazz = field.getType();
            BuilderUtils.println(builder, depth, String.format("if (%s.equals(\"%s\")) {", nameV, name));

            depth++;
            BuilderUtils.println(builder, depth, String.format("%s = %s;", childV, BuilderUtils.primitiveToObject(clazz, "this." + fieldName)));
            depth--;

            BuilderUtils.println(builder, depth, "}");
        }
        return builder.toString();
    }
}
