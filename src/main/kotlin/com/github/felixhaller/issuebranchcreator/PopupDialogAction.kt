package com.github.felixhaller.issuebranchcreator

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import git4idea.branch.GitBranchUtil
import git4idea.branch.GitBrancher
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.vcs.log.VcsLogDataKeys



class PopupDialogAction : AnAction() {

    override fun update(e: AnActionEvent) {
        // Using the event, evaluate the context, and enable or disable the action.
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project!!
        val repositories = listOf(GitBranchUtil.getCurrentRepository(project)!!)

        val issueId = Messages.showInputDialog("Jira Issue Id", "Jira issue id", null) ?: return
        val issueTitle = JiraClient(project).getIssueTitle(issueId)
        val branchNameGenerator = BranchNameGenerator(project)
        val branchName = branchNameGenerator.generateBranchName(issueId, issueTitle)

        val branchNameUser = Messages.showInputDialog("Create Git Branch", "Create Git Branch", null, branchName, null) ?: return

        GitBrancher.getInstance(project).checkoutNewBranchStartingFrom(branchNameUser, "HEAD", true, repositories, null)
//        val issueId = Messages.showInputDialog("Jira Issue Id", "Jira Issue Id", null) ?: return
//        val issueTitle = JiraClient(project).getIssueTitle(issueId)
//        val branchNameGenerator = BranchNameGenerator(project)

//        val branchName = branchNameGenerator.generateBranchName(issueId, issueTitle)

//        Messages.showMessageDialog(branchName, "Branch to be created", null)
        val repository = GitBranchUtil.getCurrentRepository(project)!!
//        val dialog = GitBranchPopup.getInstance(project, repository).asListPopup().handleSelect(true)//.showInFocusCenter()
//        GitBranchPopupActions.GitNewBranchAction.create( "branch-1234",{ t -> t }).actionPerformed(event)
//        val popup = GitBranchPopup.getInstance(project, repository).asListPopup().showInFocusCenter()
//        val popup = GitBranchPopup.getInstance(project, repository)
//        getNewBranchNameFromUser(project, listOf(repository), "Foo", "branch-1234")

//        GitBranchPopupActions.GitNewBranchAction(project, listOf(repository)).actionPerformed(event)
//        event.dataContext.getData(VcsLogDataKeys.VCS_LOG_BRANCHES)
        val dataContext = DataContext { dataId ->
            if (VcsLogDataKeys.VCS_LOG_BRANCHES.`is`(dataId)) {
                "bliblablubber"
            } else event.dataContext.getData(dataId)
        }

        val newEvent = AnActionEvent(
            event.inputEvent,
            dataContext,
            event.place,
            event.presentation,
            event.actionManager,
            event.modifiers
        )

        ActionManager.getInstance().getAction("Git.CreateNewBranch").actionPerformed(newEvent)
    }
}
