package com.github.felixhaller.issuebranchcreator

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import git4idea.branch.GitBranchUtil
import git4idea.branch.GitBrancher

@Service
class CreateBranchByIssueIdWorkflow(private val project: Project) {
    private val log: Logger = Logger.getInstance(this::class.java)

    fun invoke(issueId: String): CreateBranchByIssueIdWorkflowResult {
        val branchNameGenerator = project.service<BranchNameGenerator>()
        val jiraClient = project.service<JiraClient>()

        val gitRepository = GitBranchUtil.getCurrentRepository(project)
        if (gitRepository == null) {
            log.warn("Couldn't find Git repository for current project.")
            return CreateBranchByIssueIdWorkflowResult.Error("Couldn't find Git repository for current project.")
        }

        val issueTitle = try {
            jiraClient.getIssueTitle(issueId)
        } catch (e: Exception) {
            val message = buildString {
                appendLine("Error while contacting Jira:")
                appendLine(e.message?.lines()?.firstOrNull())
            }

            log.warn(message)
            return CreateBranchByIssueIdWorkflowResult.Error(message)
        }

        val branchName = branchNameGenerator.generateBranchName(issueId, issueTitle)
        val branchNameUser = Messages.showInputDialog("New branch name", DIALOG_TITLE, null, branchName, null)
                ?: return CreateBranchByIssueIdWorkflowResult.NoAction("User cancelled Dialog")
        if (branchNameUser.isBlank()) {
            log.warn("Branch name can't be blank")
            return CreateBranchByIssueIdWorkflowResult.Error("Branch name can't be empty")
        }

        GitBrancher.getInstance(project).checkoutNewBranchStartingFrom(branchNameUser, "HEAD", true, listOf(gitRepository), null)
        return CreateBranchByIssueIdWorkflowResult.Success(branchNameUser)
    }

    sealed class CreateBranchByIssueIdWorkflowResult {
        data class Error(val message: String) : CreateBranchByIssueIdWorkflowResult()
        data class Success(val branchName: String) : CreateBranchByIssueIdWorkflowResult()
        data class NoAction(val message: String) : CreateBranchByIssueIdWorkflowResult()
    }
}
