package io.github.vincentvibe3.gqlclient.http

import io.github.vincentvibe3.gqlclient.dsl.Mutation
import io.github.vincentvibe3.gqlclient.dsl.Operation
import io.github.vincentvibe3.gqlclient.dsl.Query
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*


class GQLClient(
    val httpClient: HttpClient = HttpClient()
) {

    @PublishedApi internal fun buildQuery(operation: Operation, operationName:String, variables:JsonObject?): String {
        val name = operationName.ifBlank {
            null
        }
        val variablesString = if (variables.isNullOrEmpty()){
            null
        } else {
            variables.toString()
        }
        return Json.encodeToString(QueryRequest(operation.toString(),name,variablesString))
    }

   suspend inline fun <reified T, reified K:GQLError> send(
       url:String,
       operation:Operation,
       variables: JsonObject?,
       operationName: String,
       headers:List<HttpHeader>
   ): Response<T, K> {
        val response = httpClient.post{
            url {
                host = url
            }
            headers {
                headers.forEach {
                    append(it.key, it.value)
                }
            }
            setBody(buildQuery(operation, operationName, variables))
        }
        val body = response.bodyAsText()
        val responseData = Json.decodeFromString<InternalResponse<K>>(body)
        val data = responseData.data?.let { Json.decodeFromJsonElement<T>(it) }
        return Response(data, responseData.errors, response)
    }

    suspend inline fun <reified T, reified K:GQLError> sendQuery(
        url:String,
        query:Query,
        variables: JsonObject? = null,
        operationName: String = "",
        headers:List<HttpHeader> = listOf()
    ): Response<T, K>{
        return send<T, K>(url, query, variables, operationName, headers)
    }

    suspend inline fun <reified T, reified K:GQLError> sendMutation(
        url:String,
        mutation:Mutation,
        variables: JsonObject? = null,
        operationName: String = "",
        headers:List<HttpHeader> = listOf()
    ): Response<T, K>{
        return send<T, K>(url, mutation, variables, operationName, headers)
    }

}