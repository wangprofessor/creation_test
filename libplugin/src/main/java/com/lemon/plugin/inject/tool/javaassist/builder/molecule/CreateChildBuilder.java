package com.lemon.plugin.inject.tool.javaassist.builder.molecule;

import com.lemon.java.atom.data.Molecule;
import com.lemon.java.atom.data.node.INode;
import com.lemon.java.atom.data.node.INodeMap;
import com.lemon.java.atom.data.node.object.IField;
import com.lemon.java.atom.data.node.object.IObjectNode;
import com.lemon.plugin.inject.tool.javaassist.IMethodBuilder;
import com.lemon.plugin.inject.tool.javaassist.builder.BuilderUtils;

import java.util.Map;

public class CreateChildBuilder implements IMethodBuilder {
    private static final String nameV = "name";
    private static final String moleculeV = "molecule";
    private static final String createMoleculeChildM = "createMoleculeChild";

    @Override
    public String buildMethod(INodeMap nodeMap) {
        int depth = 0;
        String buildString = BuilderUtils.println(depth, String.format("public %s %s(String %s) {", BuilderUtils.MoleculeC, createMoleculeChildM, nameV));
        buildString += buildBody(nodeMap);
        buildString += BuilderUtils.println(depth, "}");
        return buildString;
    }

    @Override
    public String buildBody(INodeMap nodeMap) {
        int depth = 1;

        String buildString = BuilderUtils.printLogo(nodeMap.getClazz(), createMoleculeChildM, depth);

        buildString += BuilderUtils.println(depth, String.format("%s %s = null;", BuilderUtils.MoleculeC, moleculeV));
        buildString += buildBodyInner(depth, nodeMap);
        buildString += BuilderUtils.println(depth, String.format("return %s;", moleculeV));

        return buildString;
    }

    private String buildBodyInner(int depth, INodeMap nodeMap) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, INode> entry : nodeMap.getSubNodeMap().entrySet()) {
            String name = entry.getKey();
            IObjectNode objectNode = (IObjectNode) entry.getValue();
            IField field = objectNode.getField();
            Class<?> clazz = field.getType();

            String className = null;
            boolean isArray = false;
            if (Molecule.class.isAssignableFrom(clazz)) {
                className = clazz.getName();
            } else if (clazz.isArray()) {
                isArray = true;
                Class<?> componentType = clazz.getComponentType();
                if (Molecule.class.isAssignableFrom(componentType)) {
                    className = componentType.getName();
                }
            }
            if (className == null) {
                continue;
            }

            BuilderUtils.println(builder, depth, String.format("if (%s.equals(\"%s\")) {", nameV, name));

            depth++;
            if (isArray) {
                BuilderUtils.println(builder, depth, String.format("%s = new %s();", moleculeV, className));
            } else {
                BuilderUtils.println(builder, depth, String.format("if (this.%s == null) {", name));

                depth++;
                BuilderUtils.println(builder, depth, String.format("%s = new %s();", moleculeV, className));
                depth--;

                BuilderUtils.println(builder, depth, "} else {");

                depth++;
                BuilderUtils.println(builder, depth, String.format("%s = this.%s.create();", moleculeV, name));
                depth--;

                BuilderUtils.println(builder, depth, "}");
            }
            depth--;

            BuilderUtils.println(builder, depth, "}");
        }
        return builder.toString();
    }
}
