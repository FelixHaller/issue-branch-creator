package com.github.felixhaller.issuebranchcreator.settings

class BranchNameGenerator {
    fun generateBranchName(issueId: String, issueTitle: String): String {
        val cleanedIssueTitle = cleanIssueTitle(issueTitle)
        return "$cleanedIssueTitle-$issueId"
    }

    private fun cleanIssueTitle(issueTitle: String): String {
        return issueTitle.replace(" ", "-").toLowerCase()
    }
}