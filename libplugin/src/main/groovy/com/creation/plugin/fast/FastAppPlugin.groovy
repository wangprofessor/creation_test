package com.creation.plugin.fast

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.scope.VariantScope
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class FastAppPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        AppPlugin appPlugin = project.plugins.findPlugin('com.android.application')
        project.afterEvaluate(new Action<Project>() {
            @Override
            void execute(Project it) {
                for (VariantScope variantScope : appPlugin.getVariantManager().getVariantScopes()) {
                    variantScope.transformManager.transforms.each { transform ->
                        System.out.println("professor:" + transform)
                    }
                    System.out.println("*****************************" + variantScope)
                }
            }
        })
    }
}