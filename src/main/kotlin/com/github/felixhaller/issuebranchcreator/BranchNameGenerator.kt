package com.github.felixhaller.issuebranchcreator

import com.github.felixhaller.issuebranchcreator.settings.IssueBranchCreatorSettingsService
import com.github.slugify.Slugify
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.apache.commons.text.StringSubstitutor


private const val ISSUE_ID = "issueId"
private const val CLEAN_ISSUE_TITLE = "cleanIssueTitle"

@Service
class BranchNameGenerator(project: Project) {
    private val settings = project.service<IssueBranchCreatorSettingsService>().read()
    private val slugifier = Slugify()
    private val template = settings.template

    val availablePlaceholder = listOf(ISSUE_ID, CLEAN_ISSUE_TITLE)

    fun generateBranchName(issueId: String, issueTitle: String): String {
        val cleanedIssueTitle = cleanIssueTitle(issueTitle)
        val valuesMap = mapOf(
            ISSUE_ID.toLowerCase() to issueId,
            CLEAN_ISSUE_TITLE.toLowerCase() to cleanedIssueTitle
        )
        val stringSubstitutor = StringSubstitutor(valuesMap, "{", "}")
        return stringSubstitutor.replace(template.toLowerCase())
    }

    private fun cleanIssueTitle(issueTitle: String): String {
        return slugifier.slugify(issueTitle)
    }
}
