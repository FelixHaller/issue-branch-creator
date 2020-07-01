package com.github.felixhaller.issuebranchcreator

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages


class PopupDialogAction : AnAction() {
    override fun update(e: AnActionEvent) {
        // Using the event, evaluate the context, and enable or disable the action.
    }

    override fun actionPerformed(event: AnActionEvent) {
        // Using the event, create and show a dialog
        val currentProject = event.getProject()
        val dlgMsg = StringBuffer(event.getPresentation().getText().toString() + " Selected!")
        val dlgTitle = event.getPresentation().getDescription() ?: "Foo"
        // If an element is selected in the editor, add info about it.
        val nav = event.getData(CommonDataKeys.NAVIGATABLE)
        if (nav != null) {
            dlgMsg.append(java.lang.String.format("\nSelected Element: %s", nav.toString()))
        }
//        Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon())
        Messages.showInputDialog(currentProject, "Jira Issue Id", "Jira issue id", null)
    }
}