package com.github.felixhaller.issuebranchcreator.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.xmlb.XmlSerializerUtil
import javax.swing.JComponent
import javax.swing.JPanel


class ProjectSettingsConfigurable : Configurable {
    private var mySettingsComponent: ProjectSettingsComponent? = null

    override fun isModified(): Boolean = true
    override fun getDisplayName(): String = "Issue Branch Creator"
    override fun getPreferredFocusedComponent(): JComponent = mySettingsComponent!!.preferredFocusedComponent

    override fun createComponent(): JComponent {
        mySettingsComponent = ProjectSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun apply() {
        val jiraUrl = mySettingsComponent!!.jiraUrlText ?: ""
        val username = mySettingsComponent!!.usernameText ?: ""
        val password = mySettingsComponent!!.passwordText ?: ""

        val settings = IssueBranchCreatorSettings(
            jiraUrl = jiraUrl,
            username = username,
            password = password
        )

        service<IssueBranchCreatorSettingsService>().write(settings)
    }

    override fun reset() {
        val settings = service<IssueBranchCreatorSettingsService>().read()

        mySettingsComponent!!.jiraUrlText = settings.jiraUrl
        mySettingsComponent!!.usernameText = settings.username
        mySettingsComponent!!.passwordText = settings.password
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}

@State(name = "com.github.felixhaller.issuebranchcreator.settings.ProjectSettingsState", storages = [Storage("IssueBranchCreator.xml")])
class ProjectSettingsState : PersistentStateComponent<ProjectSettingsState> {
    var jiraUrl = ""

    override fun getState(): ProjectSettingsState {
        return this
    }

    override fun loadState(state: ProjectSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: ProjectSettingsState
            get() = ServiceManager.getService(ProjectSettingsState::class.java)
    }
}

class ProjectSettingsComponent {
    val panel: JPanel
    private val myJiraUrlText = JBTextField()
    private val myUsernameText = JBTextField()
    private val myPasswordText = JBPasswordField()

    val preferredFocusedComponent: JComponent
        get() = myUsernameText

    var jiraUrlText: String?
        get() = myJiraUrlText.text
        set(newText) {
            myJiraUrlText.text = newText
        }

    var usernameText: String?
        get() = myUsernameText.text
        set(newText) {
            myUsernameText.text = newText
        }

    var passwordText: String?
        get() = myPasswordText.text
        set(newText) {
            myPasswordText.text = newText
        }

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Enter Jira URL: "), myJiraUrlText, 1, false)
            .addLabeledComponent(JBLabel("Enter Username: "), myUsernameText, 1, false)
            .addLabeledComponent(JBLabel("Enter Password: "), myPasswordText, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}