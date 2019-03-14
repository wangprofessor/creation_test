package com.lemon.plugin.inject.tool.javaassist.builder.json;

import com.lemon.java.atom.data.json.JsonMolecule;
import com.lemon.java.atom.data.node.INode;
import com.lemon.java.atom.data.node.INodeMap;
import com.lemon.java.atom.data.node.NodeUtils;
import com.lemon.java.atom.data.node.object.IField;
import com.lemon.java.atom.data.node.object.IObjectNode;
import com.lemon.plugin.inject.tool.javaassist.IMethodBuilder;
import com.lemon.plugin.inject.tool.javaassist.builder.BuilderUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class JsonBuilder implements IMethodBuilder {
    private static final String JSONObjectC = JSONObject.class.getName();
    private static final String JSONArrayC = JSONArray.class.getName();
    private static final String JSONExceptionC = JSONException.class.getName();
    private static final String jsonV = "json";
    private static final String arrayV = "array";
    private static final String toJsonM = "toJson";

    @Override
    public String buildMethod(INodeMap nodeMap) {
        int depth = 0;
        String buildString = BuilderUtils.println(depth, String.format("public void %s(%s %s) {", toJsonM, JSONObjectC, jsonV));
        buildString += buildBody(nodeMap);
        buildString += BuilderUtils.println(depth, "}");
        return buildString;
    }

    @Override
    public String buildBody(INodeMap nodeMap) {
        int depth = 1;

        String buildString = BuilderUtils.printLogo(nodeMap.getClazz(), toJsonM, depth);

        buildString += BuilderUtils.println(depth, String.format("%s %s = new %s();", JSONObjectC, jsonV, JSONObjectC));
        if (nodeMap.getSubNodeMap().size() != 0) {
            buildString += BuilderUtils.println(depth, "try {");

            depth++;
            buildString += buildBodyInner(depth, nodeMap);
            depth--;

            buildString += BuilderUtils.println(depth, String.format("} catch (%s ignore) {}", JSONExceptionC));
        }
        buildString += BuilderUtils.println(depth, String.format("return %s;", jsonV));

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
                BuilderUtils.println(builder, depth, String.format("%s.put(\"%s\", this.%s);", jsonV, key, fieldName));
            } else {
                if (JsonMolecule.class.isAssignableFrom(clazz)) {
                    BuilderUtils.println(builder, depth, String.format("%s.put(\"%s\", this.%s == null ? null : this.%s.%s());", jsonV, key, fieldName, fieldName, toJsonM));
                } else if (clazz.isArray()) {
                    builder.append(buildStringForArray(depth, key, field));
                } else {
                    BuilderUtils.println(builder, depth, String.format("%s.put(\"%s\", this.%s);", jsonV, key, fieldName));
                }
            }
        }
        return builder.toString();
    }

    private String buildStringForArray(int depth, String key, IField field) {
        String fieldName = field.getName();
        Class<?> type = field.getType();
        Class<?> componentType = type.getComponentType();
        String componentTypeName = componentType.getName();

        String result = BuilderUtils.println(depth, String.format("if (this.%s != null) {", fieldName));

        depth++;
        result += BuilderUtils.println(depth, String.format("%s %s = new %s();", JSONArrayC, arrayV, JSONArrayC));
        result += BuilderUtils.println(depth, jsonV + String.format(".put(\"%s\", %s);", key, arrayV));
        result += BuilderUtils.println(depth, String.format("for (int i = 0; i < this.%s.length; i++) {", fieldName));

        depth++;
        result += BuilderUtils.println(depth, String.format("%s %s = this.%s[i];", componentTypeName, BuilderUtils.EACH, fieldName));
        if (NodeUtils.isPrimitive(componentType)) {
            result += BuilderUtils.println(depth, arrayV + String.format(".put(%s);", BuilderUtils.EACH));
        } else {
            result += BuilderUtils.println(depth, arrayV + String.format(".put(%s.%s());", BuilderUtils.EACH, toJsonM));
        }
        depth--;

        result += BuilderUtils.println(depth, "}");
        depth--;

        result += BuilderUtils.println(depth, "}");
        return result;
    }
}
