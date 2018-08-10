package com.ryan.log

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 查看transform 中的 invocation
 */
class HMTPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ILog log = new Log2Console()
        HMTConfig hmtConfig = project.extensions.create("HMTConfig",
                HMTConfig.class)
        project.task("printConfig") {
            doLast {
                println "pkg = $hmtConfig.includePkg"
                println "clazz = $hmtConfig.excludeClass"
            }
        }

        if (hmtConfig.enableHMT) {
            log.d("HMTPlugin", "HMT Transform register start")
            boolean hasPlugin = project.plugins.hasPlugin(AppPlugin)

            if (hasPlugin) {
                AppExtension app = project.extensions.findByName('android')
                if (!app) {
                    println("AppExtension is null")
                    return
                }

                app.registerTransform(new HMTTransform(project, hmtConfig, log, app))

            }

            log.d("HMTPlugin", "HMT Transform register end")

        }
    }

}