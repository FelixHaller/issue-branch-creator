package com.github.felixhaller.issuebranchcreator.settings

import com.github.slugify.Slugify
import org.apache.commons.text.StringSubstitutor


class BranchNameGenerator {

    val slugifier = Slugify()

    val template = "{cleanIssueTitle}-{issueID}"

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