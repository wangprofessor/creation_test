package com.creation.plugin.fast

import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.scope.VariantScope

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry

import javax.inject.Inject

class FastLibPlugin extends LibraryPlugin {
    @Inject
    FastLibPlugin(ToolingModelBuilderRegistry registry) {
        super(registry)
    }

    @Override
    protected void pluginSpecificApply(Project project) {
        super.pluginSpecificApply(project)
        project.afterEvaluate(new Action<Project>() {
            @Override
            void execute(Project it) {
                for (VariantScope variantScope : getVariantManager().getVariantScopes()) {
                    variantScope.transformManager.transforms.each { transform ->
                        System.out.println("professor:" + transform)
                    }
                    System.out.println("*****************************" + variantScope)
                }
            }
        })
    }
}