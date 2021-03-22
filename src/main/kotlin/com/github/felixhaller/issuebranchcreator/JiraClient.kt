package com.github.felixhaller.issuebranchcreator

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.felixhaller.issuebranchcreator.settings.IssueBranchCreatorSettingsService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import okhttp3.Credentials
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit


private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

private val mapper = ObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

@Service
class JiraClient(private val project: Project) {
    fun getIssueTitle(issueId: String): String {
        val settings = project.service<IssueBranchCreatorSettingsService>().read()

        val httpUrl = "${settings.jiraUrl}/rest/api/latest/issue/$issueId".toHttpUrl()
        return client.getAndMapWithBasicAuth<IssueResponse>(httpUrl, mapper, settings.username, settings.password).fields.summary
    }
}

data class IssueResponse(
        val fields: Fields
) {
    data class Fields(
            val summary: String
    )
}

inline fun <reified T> OkHttpClient.getAndMapWithBasicAuth(url: HttpUrl, mapper: ObjectMapper, username: String, password: String): T {
    val credentials = Credentials.basic(username, password)
    val request = Request.Builder().addHeader("Authorization", credentials).url(url).get().build()
    return callAndMap(this, request, url, mapper)
}

inline fun <reified T> callAndMap(httpClient: OkHttpClient, request: Request, url: HttpUrl, mapper: ObjectMapper): T {
    try {
        return httpClient.newCall(request).execute().use {
            if (it.isSuccessful) {
                val body = it.body ?: throw Exception("Received empty response $url")
                mapper.readValue<T>(body.byteStream())
            } else {
                throw Exception("Received code ${it.code} from url $url. Body: ${it.body?.string()}")
            }
        }
    } catch (ex: JsonProcessingException) {
        throw Exception("Failed to deserialize body of $url.", ex)
    } catch (ex: IOException) {
        throw Exception("Failed to sent request to $url due to IO issues", ex)
    }
}
