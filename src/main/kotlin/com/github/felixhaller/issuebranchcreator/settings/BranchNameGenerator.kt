package com.github.felixhaller.issuebranchcreator.settings

import com.github.slugify.Slugify

class BranchNameGenerator {

    var slugifier = Slugify()

    fun generateBranchName(issueId: String, issueTitle: String): String {
        val cleanedIssueTitle = cleanIssueTitle(issueTitle)
        return "$cleanedIssueTitle-$issueId"
    }

    private fun cleanIssueTitle(issueTitle: String): String {
        return slugifier.slugify(issueTitle)
    }
}