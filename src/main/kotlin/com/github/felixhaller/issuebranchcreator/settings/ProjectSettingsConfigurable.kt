package com.github.felixhaller.issuebranchcreator.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.xmlb.XmlSerializerUtil
import javax.swing.JComponent
import javax.swing.JPanel


class ProjectSettingsConfigurable : Configurable {
    private val SYSTEM = "IssueBranchCreator"
    private val CREDENTIALS_JIRA = "IssueBranchCreator.JiraLogin"

    private var mySettingsComponent: ProjectSettingsComponent? = null

    override fun isModified(): Boolean = true
    override fun getDisplayName(): String = "Issue Branch Creator"
    override fun getPreferredFocusedComponent(): JComponent = mySettingsComponent!!.preferredFocusedComponent

    override fun createComponent(): JComponent {
        mySettingsComponent = ProjectSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun apply() {
        val settings: ProjectSettingsState = ProjectSettingsState.instance

        settings.jiraUrl = mySettingsComponent!!.jiraUrlText ?: ""
        val username = mySettingsComponent!!.userNameText
        val password = mySettingsComponent!!.passwordText

        val credentialAttributes = createCredentialAttributes(CREDENTIALS_JIRA)
        val credentials = Credentials(username, password)
        PasswordSafe.instance.set(credentialAttributes, credentials)
    }

    override fun reset() {
        val settings: ProjectSettingsState = ProjectSettingsState.instance
        mySettingsComponent!!.jiraUrlText = settings.jiraUrl

        val credentialAttributes = createCredentialAttributes(CREDENTIALS_JIRA)
        val credentials = PasswordSafe.instance.get(credentialAttributes)
        if (credentials != null) {
            mySettingsComponent!!.userNameText = credentials.getPasswordAsString()
            mySettingsComponent!!.passwordText = credentials.userName
        }
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }

    private fun createCredentialAttributes(key: String): CredentialAttributes {
        return CredentialAttributes(generateServiceName(SYSTEM, key))
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
    private val myUserNameText = JBTextField()
    private val myPasswordText = JBTextField()

    val preferredFocusedComponent: JComponent
        get() = myUserNameText

    var jiraUrlText: String?
        get() = myJiraUrlText.text
        set(newText) {
            myJiraUrlText.text = newText
        }

    var userNameText: String?
        get() = myUserNameText.text
        set(newText) {
            myUserNameText.text = newText
        }

    var passwordText: String?
        get() = myPasswordText.text
        set(newText) {
            myPasswordText.text = newText
        }

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Enter Jira URL: "), myJiraUrlText, 1, false)
            .addLabeledComponent(JBLabel("Enter UserName: "), myUserNameText, 1, false)
            .addLabeledComponent(JBLabel("Enter Password: "), myPasswordText, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}