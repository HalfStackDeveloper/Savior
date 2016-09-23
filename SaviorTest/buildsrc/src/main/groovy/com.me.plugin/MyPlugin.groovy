package com.me.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MyPlugin implements Plugin<Project> {

    void apply(Project project) {
        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new MyTransform(project))
    }
}