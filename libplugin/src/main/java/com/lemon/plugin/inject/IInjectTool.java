package com.lemon.plugin.inject;

import java.io.File;

public interface IInjectTool {
    void injectDir(File dir);
    void injectFile(File dir, File file);
}
