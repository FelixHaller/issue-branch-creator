package com.github.felixhaller.issuebranchcreator.services

import com.intellij.openapi.project.Project
import com.github.felixhaller.issuebranchcreator.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
