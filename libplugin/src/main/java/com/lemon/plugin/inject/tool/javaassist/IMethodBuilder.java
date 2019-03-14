package com.lemon.plugin.inject.tool.javaassist;

import com.lemon.java.atom.data.node.INodeMap;

public interface IMethodBuilder {
    String buildMethod(INodeMap nodeMap);
    String buildBody(INodeMap nodeMap);
}
