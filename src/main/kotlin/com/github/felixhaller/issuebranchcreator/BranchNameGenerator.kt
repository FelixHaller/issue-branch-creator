package com.github.felixhaller.issuebranchcreator

import com.github.felixhaller.issuebranchcreator.settings.IssueBranchCreatorSettingsService
import com.github.slugify.Slugify
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.apache.commons.text.StringSubstitutor


class BranchNameGenerator(project: Project) {

    private val settings = project.service<IssueBranchCreatorSettingsService>().read()
    private val slugifier = Slugify()
    private val template = settings.template

    fun generateBranchName(issueId: String, issueTitle: String): String {
        val cleanedIssueTitle = cleanIssueTitle(issueTitle)
        val valuesMap = mapOf(
            "issueid" to issueId,
            "cleanissuetitle" to cleanedIssueTitle
        )
        val stringSubstitutor = StringSubstitutor(valuesMap, "{", "}")
        return stringSubstitutor.replace(template.toLowerCase())
    }

    private fun cleanIssueTitle(issueTitle: String): String {
        return slugifier.slugify(issueTitle)
    }
}