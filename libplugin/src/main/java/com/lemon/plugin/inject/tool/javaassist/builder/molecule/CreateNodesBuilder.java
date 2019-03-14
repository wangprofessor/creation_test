package com.lemon.plugin.inject.tool.javaassist.builder.molecule;

import com.lemon.java.atom.data.Molecule;
import com.lemon.java.atom.data.MoleculeField;
import com.lemon.java.atom.data.node.INode;
import com.lemon.java.atom.data.node.INodeMap;
import com.lemon.java.atom.data.node.NodeUtils;
import com.lemon.java.atom.data.node.object.IField;
import com.lemon.java.atom.data.node.object.IObjectNode;
import com.lemon.java.atom.data.node.object.ObjectNodeArray;
import com.lemon.java.atom.data.node.object.ObjectNodeCreator;
import com.lemon.java.atom.data.node.object.ObjectNodeHolder;
import com.lemon.plugin.inject.tool.javaassist.IMethodBuilder;
import com.lemon.plugin.inject.tool.javaassist.builder.BuilderUtils;

import java.util.Map;

public class CreateNodesBuilder implements IMethodBuilder {
    private static final String createNodeMapM = "createNodeMap";

    private static final String MoleculeFieldC = MoleculeField.class.getName();
    private static final String IObjectNodeC = IObjectNode.class.getName();
    private static final String ObjectNodeArrayC = ObjectNodeArray.class.getName();
    private static final String INodeMapC = INodeMap.class.getName();
    private static final String ObjectNodeHolderC = ObjectNodeHolder.class.getName();
    private static final String ObjectNodeCreatorC = ObjectNodeCreator.class.getName();
    private static final String INodeC = INode.class.getName();

    private static final String getNodeCreatorM = "getNodeCreator";
    private static final String createRootNodesM = "createRootNodes";
    private static final String createSubNodeM = "createSubNode";
    private static final String setNameM = "setName";
    private static final String setTagM = "setTag";
    private static final String setFieldM = "setField";
    private static final String setArraySubM = "setArraySub";
    private static final String setVariationM = "setVariation";
    private static final String setMapToArrayM = "setMapToArray";
    private static final String mapSetTypeM = "mapSetType";
    private static final String arraySetTypeM = "arraySetType";

    private static final String nodeMapV = "nodeMap";
    private static final String creatorV = "creator";
    private static final String childV = "child";
    private static final String childNodeV = "childNode";
    private static final String childArrayNodeV = "childArrayNode";
    private static final String typeV = "type";
    private static final String fieldV = "field";
    private static final String eachArraySubV = "eachArraySub";

    @Override
    public String buildMethod(INodeMap nodeMap) {
        int depth = 0;
        String buildString = BuilderUtils.println(depth, String.format("public %s %s() {", INodeMapC, createNodeMapM));
        buildString += buildBody(nodeMap);
        buildString += BuilderUtils.println(depth, "}");
        return buildString;
    }

    @Override
    public String buildBody(INodeMap nodeMap) {
        int depth = 1;

        String  buildString = BuilderUtils.printLogo(nodeMap.getClazz(), createNodeMapM, depth);

        buildString += BuilderUtils.println(depth, String.format("%s %s = (%s) new %s().%s();", ObjectNodeCreatorC, creatorV, ObjectNodeCreatorC, ObjectNodeHolderC, getNodeCreatorM));
        buildString += BuilderUtils.println(depth, String.format("%s %s = %s.%s(this);", INodeMapC, nodeMapV, creatorV, createRootNodesM));
        buildString += BuilderUtils.println(depth, String.format("%s %s;", BuilderUtils.MoleculeC, childV));
        buildString += BuilderUtils.println(depth, String.format("%s %s;", IObjectNodeC, childNodeV));
        buildString += BuilderUtils.println(depth, String.format("%s %s;", ObjectNodeArrayC, childArrayNodeV));
        buildString += BuilderUtils.println(depth, String.format("%s %s;", MoleculeFieldC, fieldV));
        buildString += BuilderUtils.println(depth, String.format("Object %s;", typeV));

        buildString += BuilderUtils.println();
        buildString += buildBodyInner(depth, nodeMap);

        buildString += BuilderUtils.println(depth, String.format("return %s;", nodeMapV));

        return buildString;
    }

    private String buildBodyInner(int depth, INodeMap nodeMap) {
        StringBuilder builder = new StringBuilder();

        BuilderUtils.println(builder, depth, setupNodeMap((IObjectNode) nodeMap));

        for (Map.Entry<String, INode> entry : nodeMap.getSubNodeMap().entrySet()) {
            IObjectNode objectNode = (IObjectNode) entry.getValue();
            IField field = objectNode.getField();

            Class<?> clazz = field.getType();
            String name = objectNode.name();
            String fieldName = field.getName();

            if (clazz.isArray()) {
                buildArrayString(depth, builder, clazz, name, fieldName);
            } else {
                if (NodeUtils.isPrimitive(clazz)) {
                    BuilderUtils.println(builder, depth, writeType(clazz, fieldName));
                    BuilderUtils.println(builder, depth, writeField(clazz, fieldName));
                    String node = createSubNode(name, name, false);
                    BuilderUtils.println(builder, depth, putIntoMap(node));
                } else if (Molecule.class.isAssignableFrom(clazz)) {
                    BuilderUtils.println(builder, depth, initChild(clazz, fieldName));
                    BuilderUtils.println(builder, depth, getChildNodeMap());
                    BuilderUtils.println(builder, depth, writeField(clazz, fieldName));
                    BuilderUtils.println(builder, depth, setupChildNodeMap(name, false));
                    BuilderUtils.println(builder, depth, putIntoMap(childNodeV));
                }
            }
            BuilderUtils.println(builder);
        }
        return builder.toString();
    }

    private String setupNodeMap(IObjectNode objectNode) {
        String tagString = objectNode.tag() == null ? "%s" : "\"%s\"";
        return String.format("((%s) %s).%s(" + tagString + ");", IObjectNodeC, nodeMapV, setTagM, objectNode.tag()) +
                String.format("%s.%s(%s);", nodeMapV, setMapToArrayM, objectNode.isMapToArray());
    }

    private void buildArrayString(int depth, StringBuilder builder, Class<?> clazz, String name, String fieldName) {
        BuilderUtils.println(builder, depth, writeType(clazz, fieldName));
        BuilderUtils.println(builder, depth, writeField(clazz, fieldName));
        String childArrayNodeValue = createSubNode(name, name, false);
        BuilderUtils.println(builder, depth, writeChildArrayNode(childArrayNodeValue));

        Class<?> componentType = clazz.getComponentType();
        BuilderUtils.println(builder, depth, String.format("if (this.%s == null) {", fieldName));

        depth++;
        BuilderUtils.println(builder, depth, writeTypeForArray(componentType));
        BuilderUtils.println(builder, depth, writeFieldForArray());
        String arraySubValue = createSubNode(null, null, true);
        BuilderUtils.println(builder, depth, String.format("%s %s = %s;", INodeC, BuilderUtils.EACH, arraySubValue));
        BuilderUtils.println(builder, depth, String.format("%s.%s(0, %s);", childArrayNodeV, arraySetTypeM, BuilderUtils.EACH));
        depth--;

        BuilderUtils.println(builder, depth, "} else {");

        depth++;
        BuilderUtils.println(builder, depth, String.format("for(int i = 0; i < this.%s.length; i++) {", fieldName));

        depth++;
        if (NodeUtils.isPrimitive(componentType)) {
            BuilderUtils.println(builder, depth, String.format("%s %s = this.%s[i];", componentType.getName(), eachArraySubV, fieldName));
            BuilderUtils.println(builder, depth, writeTypeForArray(componentType, eachArraySubV));
            BuilderUtils.println(builder, depth, writeFieldForArray());
            BuilderUtils.println(builder, depth, String.format("%s %s = %s;", INodeC, BuilderUtils.EACH, createSubNode(null, null, true)));
            BuilderUtils.println(builder, depth, String.format("%s.%s(i, %s);", childArrayNodeV, arraySetTypeM, BuilderUtils.EACH));
        } else {
            BuilderUtils.println(builder, depth, String.format("%s %s = this.%s[i];", componentType.getName(), eachArraySubV, fieldName));
            BuilderUtils.println(builder, depth, initChildForArray(componentType));
            BuilderUtils.println(builder, depth, getChildNodeMap());
            BuilderUtils.println(builder, depth, writeFieldForArray());
            BuilderUtils.println(builder, depth, setupChildNodeMap(null, true));
            BuilderUtils.println(builder, depth, String.format("%s.%s(i, %s);", childArrayNodeV, arraySetTypeM, childNodeV));

        }
        depth--;

        BuilderUtils.println(builder, depth, "}");
        depth--;

        BuilderUtils.println(builder, depth, "}");

        BuilderUtils.println(builder, depth, putIntoMap(childArrayNodeV));
    }

    private String putIntoMap(String node) {
        return String.format("%s.%s(%s);", nodeMapV, mapSetTypeM, node);
    }

    private String writeType(Class<?> clazz, String fieldName) {
        if (clazz.isPrimitive()) {
            return String.format("%s = %s;", typeV, BuilderUtils.primitiveToObject(clazz, "this." + fieldName));
        }
        return String.format("if (this.%s == null) { %s = %s.class; } else { %s = this.%s; }", fieldName, typeV, getClassString(clazz), typeV, fieldName);
    }

    private String writeField(Class<?> clazz, String fieldName) {
        return String.format("%s = new %s(%s, \"%s\");", fieldV, MoleculeFieldC, getClass(clazz, fieldName), fieldName);
    }

    private String createSubNode(String name, String tag, boolean isArraySub) {
        String nameString = name == null ? "%s" : "\"%s\"";
        String tagString = tag == null ? "%s" : "\"%s\"";
        return String.format("%s.%s(%s, " + nameString + ", " + tagString + ", %s, %s)", creatorV, createSubNodeM, typeV, name, tag, isArraySub, fieldV);
    }

    private String getClass(Class<?> clazz, String fieldName) {
        if (NodeUtils.isPrimitive(clazz)) {
            return clazz.getName() + ".class";
        } else if (clazz.isArray()) {
            return getClassString(clazz) + ".class";
        }
        return String.format("this.%s == null ? %s.class : this.%s.getClass()", fieldName, getClassString(clazz), fieldName);
    }

    private String getClassString(Class<?> clazz) {
        String classString;
        if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            classString = componentType.getName() + "[]";
        } else {
            classString = clazz.getName();
        }
        return classString;
    }

    private String initChildForArray(Class<?> clazz) {
        return String.format("%s = %s == null ? new %s() : %s;", childV, eachArraySubV, clazz.getName(), eachArraySubV);
    }

    private String initChild(Class<?> clazz, String fieldName) {
        return String.format("%s = this.%s == null ? new %s() : this.%s;", childV, fieldName, clazz.getName(), fieldName);
    }

    private String getChildNodeMap() {
        return String.format("%s = %s.%s();", childNodeV, childV, createNodeMapM);
    }

    private String setupChildNodeMap(String name, boolean isArraySub) {
        String nameString = name == null ? "%s" : "\"%s\"";
        return String.format("%s.%s(" + nameString + ");", childNodeV, setNameM, name) +
                String.format("%s.%s(%s);", childNodeV, setArraySubM, isArraySub) +
                String.format("%s.%s(%s);", childNodeV, setFieldM, fieldV);
    }

    private String writeChildArrayNode(String value) {
        return String.format("%s = (%s) %s;", childArrayNodeV, ObjectNodeArrayC, value);
    }

    private String writeTypeForArray(Class<?> clazz) {
        return String.format("%s = %s.class;", typeV, getClassString(clazz));
    }

    private String writeTypeForArray(Class<?> clazz, String typeValue) {
        if (clazz.isPrimitive()) {
            return String.format("%s = %s;", typeV, BuilderUtils.primitiveToObject(clazz, typeValue));
        }
        return String.format("%s = %s;", typeV, typeValue);
    }

    private String writeFieldForArray() {
        return String.format("%s = null;", fieldV);
    }
}
