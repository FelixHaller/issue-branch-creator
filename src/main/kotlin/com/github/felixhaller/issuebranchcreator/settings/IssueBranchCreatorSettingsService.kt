package com.github.felixhaller.issuebranchcreator.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

private const val SYSTEM = "IssueBranchCreator"
private const val CREDENTIALS_JIRA = "IssueBranchCreator.JiraLogin"

@Service
class IssueBranchCreatorSettingsService(private val project: Project) {
    fun read(): IssueBranchCreatorSettings {
        val projectSettingsState = project.service<ProjectSettingsState>()
        val credentials = PasswordSafe.instance.get(createCredentialAttributes())

        return IssueBranchCreatorSettings(
            jiraUrl = projectSettingsState.jiraUrl,
            username = credentials?.userName ?: "",
            password = credentials?.getPasswordAsString() ?: ""
        )
    }

    fun write(settings: IssueBranchCreatorSettings) {
        val projectSettingsState = project.service<ProjectSettingsState>()
        projectSettingsState.jiraUrl = settings.jiraUrl

        val credentials = Credentials(settings.username, settings.password)
        PasswordSafe.instance.set(createCredentialAttributes(), credentials)
    }

    private fun generateKey(): String {
        return "${project.name}:$CREDENTIALS_JIRA"
    }

    private fun createCredentialAttributes(): CredentialAttributes {
        return CredentialAttributes(
            generateServiceName(subsystem = SYSTEM, key = generateKey())


        )
    }
}

data class IssueBranchCreatorSettings(
    val jiraUrl: String,
    val username: String,
    val password: String
)