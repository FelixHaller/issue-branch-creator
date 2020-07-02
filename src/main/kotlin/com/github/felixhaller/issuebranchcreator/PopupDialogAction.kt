package com.github.felixhaller.issuebranchcreator

import com.github.felixhaller.issuebranchcreator.settings.BranchNameGenerator
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages


class PopupDialogAction : AnAction() {

    private val branchNameGenerator = BranchNameGenerator()

    override fun update(e: AnActionEvent) {
        // Using the event, evaluate the context, and enable or disable the action.
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project!!
        val issueId = Messages.showInputDialog("Jira Issue Id", "Jira issue id", null) ?: return
        val issueTitle = JiraClient(project).getIssueTitle(issueId)

        val branchName = branchNameGenerator.generateBranchName(issueId, issueTitle)

        Messages.showMessageDialog(branchName, "Branch to be created", null)
    }
}