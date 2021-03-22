package com.github.felixhaller.issuebranchcreator

import com.github.felixhaller.issuebranchcreator.CreateBranchByIssueIdWorkflow.CreateBranchByIssueIdWorkflowResult
import com.github.felixhaller.issuebranchcreator.settings.IssueBranchCreatorSettingsService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.Messages


const val DIALOG_TITLE = "Issue Branch Creator"

class CreateBranchByIssueIdAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val settings = project.service<IssueBranchCreatorSettingsService>().read()

        if (settings.jiraUrl.isBlank()) {
            showErrorDialog("Please configure the plugin under: Files -> Settings -> Tools -> Issue Branch Creator")
            return
        }

        val issueId = Messages.showInputDialog("Jira issue Id:", DIALOG_TITLE, null) ?: return

        val result = project.service<CreateBranchByIssueIdWorkflow>().invoke(issueId)
        when (result) {
            is CreateBranchByIssueIdWorkflowResult.Error -> showErrorDialog(result.message)
            is CreateBranchByIssueIdWorkflowResult.NoAction -> Unit
            is CreateBranchByIssueIdWorkflowResult.Success -> Unit
        }
    }

    private fun showErrorDialog(message: String) {
        Messages.showErrorDialog(message, DIALOG_TITLE)
    }
}
