package com.github.felixhaller.issuebranchcreator.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.Service

private const val SYSTEM = "IssueBranchCreator"
private const val CREDENTIALS_JIRA = "IssueBranchCreator.JiraLogin"

@Service
class IssueBranchCreatorSettingsService {
    fun read(): IssueBranchCreatorSettings {
        val projectSettingsState = ProjectSettingsState.instance
        val credentials = PasswordSafe.instance.get(createCredentialAttributes(CREDENTIALS_JIRA))

        return IssueBranchCreatorSettings(
            jiraUrl = projectSettingsState.jiraUrl,
            username = credentials?.userName ?: "",
            password = credentials?.getPasswordAsString() ?: ""
        )
    }

    fun write(settings: IssueBranchCreatorSettings) {
        val projectSettingsState = ProjectSettingsState.instance
        projectSettingsState.jiraUrl = settings.jiraUrl

        val credentials = Credentials(settings.username, settings.password)
        PasswordSafe.instance.set(createCredentialAttributes(CREDENTIALS_JIRA), credentials)
    }

    private fun createCredentialAttributes(key: String): CredentialAttributes {
        return CredentialAttributes(
            generateServiceName(
                SYSTEM,
                key
            )
        )
    }
}

data class IssueBranchCreatorSettings(
    val jiraUrl: String,
    val username: String,
    val password: String
)