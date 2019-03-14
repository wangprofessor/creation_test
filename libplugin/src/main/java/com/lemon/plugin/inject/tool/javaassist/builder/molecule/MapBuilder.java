package com.lemon.plugin.inject.tool.javaassist.builder.molecule;

import com.lemon.java.atom.data.Molecule;
import com.lemon.java.atom.data.node.INode;
import com.lemon.java.atom.data.node.INodeMap;
import com.lemon.java.atom.data.node.NodeUtils;
import com.lemon.java.atom.data.node.object.IField;
import com.lemon.java.atom.data.node.object.IObjectNode;
import com.lemon.plugin.inject.tool.javaassist.IMethodBuilder;
import com.lemon.plugin.inject.tool.javaassist.builder.BuilderUtils;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder implements IMethodBuilder {
    private static final String HashMapC = HashMap.class.getName();
    private static final String mapV = "map";
    private static final String toMapM = "toMap";

    @Override
    public String buildMethod(INodeMap nodeMap) {
        int depth = 0;
        String buildString = BuilderUtils.println(depth, String.format("public %s %s() {", HashMapC, toMapM));
        buildString += buildBody(nodeMap);
        buildString += BuilderUtils.println(depth, "}");
        return buildString;
    }

    @Override
    public String buildBody(INodeMap nodeMap) {
        int depth = 1;

        String buildString = BuilderUtils.printLogo(nodeMap.getClazz(), toMapM, depth);

        buildString += BuilderUtils.println(depth, String.format("%s %s = new %s();", HashMapC, mapV, HashMapC));
        buildString += buildBodyInner(depth, nodeMap);
        buildString += BuilderUtils.println(depth, String.format("return %s;", mapV));

        return buildString;
    }

    private String buildBodyInner(int depth, INodeMap nodeMap) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, INode> entry : nodeMap.getSubNodeMap().entrySet()) {
            String key = entry.getKey();
            IObjectNode objectNode = (IObjectNode) entry.getValue();
            IField field = objectNode.getField();
            Class<?> clazz = field.getType();
            String fieldName = field.getName();
            if (NodeUtils.isPrimitive(clazz)) {
                BuilderUtils.println(builder, depth, String.format("%s.put(\"%s\", %s);", mapV, key, BuilderUtils.primitiveToObject(clazz, "this." + fieldName)));
            } else {
                if (Molecule.class.isAssignableFrom(clazz)) {
                    BuilderUtils.println(builder, depth, String.format("%s.put(\"%s\", this.%s.%s());", mapV, key, fieldName, toMapM));
                } else {
                    BuilderUtils.println(builder, depth, String.format("%s.put(\"%s\", this.%s);", mapV, key, fieldName));
                }
            }
        }
        return builder.toString();
    }
}
