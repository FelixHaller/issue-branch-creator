package com.github.felixhaller.issuebranchcreator

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages


class PopupDialogAction : AnAction() {
    override fun update(e: AnActionEvent) {
        // Using the event, evaluate the context, and enable or disable the action.
    }

    override fun actionPerformed(event: AnActionEvent) {
        val issueId = Messages.showInputDialog("Jira Issue Id", "Jira issue id", null) ?: return
        val username = Messages.showInputDialog("Username:", "Jira Credentials", null) ?: return
        val password = Messages.showPasswordDialog("Password for $username:", "Jira Credentials") ?: return
        val issueTitle = JiraClient()
            .getIssueTitle(issueId, username, password)
        Messages.showMessageDialog(issueTitle, "Issue Title", null)
    }
}