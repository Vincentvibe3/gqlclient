package io.github.vincentvibe3.gqlclient.http

import io.github.vincentvibe3.gqlclient.dsl.Query
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

class GQLClient(engine: HttpClientEngine) {

    @PublishedApi internal val httpClient = HttpClient(engine)

    @PublishedApi internal fun buildQuery(query: Query, operationName:String, variables:JsonObject?): String {
        val name = operationName.ifBlank {
            null
        }
        val variablesString = if (variables.isNullOrEmpty()){
            null
        } else {
            variables.toString()
        }
        return Json.encodeToString(QueryRequest(query.toString(),name,variablesString))
    }

   suspend inline fun <reified T, reified K:GQLError> send(url:String, query:Query, variables: JsonObject?=null, operationName: String=""): Response<T, K> {
        val response = httpClient.post{
            url {
                host = url
            }
            setBody(buildQuery(query, operationName, variables))
        }
        val body = response.bodyAsText()
        val responseData = Json.decodeFromString<InternalResponse<K>>(body)
        val data = responseData.data?.let { Json.decodeFromJsonElement<T>(it) }
        return Response(data, responseData.errors, response)
    }

}