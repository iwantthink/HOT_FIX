package com.ryan.log

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 查看transform 中的 invocation
 */
class Log implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("Log Transform Start")
        LogConfig logConfig = project.extensions.create("LogConfig",
                LogConfig.class)
        project.task("printConfig") {
            doLast {
                println "pkg = $logConfig.includePkg"
                println "clazz = $logConfig.includeClass"
            }
        }

        def hasPlugin = project.plugins.hasPlugin(AppPlugin)

        if (hasPlugin) {
            AppExtension app = project.extensions.findByName('android')
            if (!app) {
                println("AppExtension is null")
                return
            }
            app.registerTransform(new MyTransform(project, logConfig))

        }

        println("Log Transform End")

    }

}