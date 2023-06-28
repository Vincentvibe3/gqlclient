package io.github.vincentvibe3.gqlclient.http

import io.github.vincentvibe3.gqlclient.dsl.Mutation
import io.github.vincentvibe3.gqlclient.dsl.Operation
import io.github.vincentvibe3.gqlclient.dsl.Query
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

/**
 *
 * Client used to send HTTP POST requests to an api
 *
 * @param httpClient The [HttpClient]
 * used to send requests. Uses an automatically configured [HttpClient] by default.
 *
 * @see HttpClient
 */
class GQLClient(
    val httpClient: HttpClient = HttpClient()
) {

    /**
     * Serializes data into a json payload for HTTP POST
     *
     * @see QueryRequest
     */
    @PublishedApi internal fun buildQuery(operation: Operation, operationName:String, variables:JsonObject?): String {
        val name = operationName.ifBlank {
            null
        }
        return Json.encodeToString(QueryRequest(operation.toString(),name,variables))
    }

    /**
     * Sends an operation by HTTP POST
     *
     * @see sendMutation
     * @see sendQuery
     */
    @PublishedApi internal suspend inline fun send(
       url:String,
       operation:Operation,
       variables: JsonObject?,
       operationName: String,
       headers:List<HttpHeader>
   ): Pair<String, HttpResponse> {
        val response = httpClient.post(url){
            headers {
                headers.forEach {
                    append(it.name, it.value)
                }
                append("Content-Type", "application/json")
            }
            setBody(buildQuery(operation, operationName, variables))
        }
        val body = response.bodyAsText()
        return body to response
    }

    /**
     * Sends a query to a specified HTTP endpoint as a POST request.
     *
     * @return Returns a [Response] serialized from the json received.
     *
     * @param T Type to serialize the data into.
     * @param E Type to serialize any errors into.
     * @param url URL to send the request to.
     * @param query Query to send.
     * @param variables Values of variables used in query to send (Optional).
     * @param operationName Name of the operation performed (Optional).
     * @param headers Headers to send along with the HTTP request as a list of [HttpHeader].
     *
     */
    suspend inline fun <reified T, reified E:GQLError> sendQuery(
        url:String,
        query:Query,
        variables: JsonObject? = null,
        operationName: String = "",
        headers:List<HttpHeader> = listOf()
    ): Response<T, E>{
        val (body, httpResponse) = send(url, query, variables, operationName, headers)
        val responseData = Json.decodeFromString<InternalResponse<E>>(body)
        val data = responseData.data?.let { Json.decodeFromJsonElement<T>(it) }
        return Response(data, responseData.errors, httpResponse)
    }

    /**
     * Sends a mutation to a specified HTTP endpoint as a POST request.
     *
     * @return Returns a [Response] serialized from the json received.
     *
     * @param T Type to serialize the data into.
     * @param E Type to serialize any errors into.
     * @param url URL to send the request to.
     * @param mutation Mutation to send.
     * @param variables Values of variables used in query to send (Optional).
     * @param operationName Name of the operation performed (Optional).
     * @param headers Headers to send along with the HTTP request as a list of [HttpHeader].
     *
     */
    suspend inline fun <reified T, reified E:GQLError> sendMutation(
        url:String,
        mutation:Mutation,
        variables: JsonObject? = null,
        operationName: String = "",
        headers:List<HttpHeader> = listOf()
    ): Response<T, E>{
        val (body, httpResponse) = send(url, mutation, variables, operationName, headers)
        val responseData = Json.decodeFromString<InternalResponse<E>>(body)
        val data = responseData.data?.let { Json.decodeFromJsonElement<T>(it) }
        return Response(data, responseData.errors, httpResponse)
    }

    suspend fun stringSendQuery(
        url:String,
        mutation:Query,
        variables: JsonObject? = null,
        operationName: String = "",
        headers:List<HttpHeader> = listOf()
    ):StringResponse{
        val (body, httpResponse) = send(url, mutation, variables, operationName, headers)
        val responseData = Json.decodeFromString<InternalResponse<JsonObject>>(body)
        val data = responseData.data.toString()
        val errors = Json.encodeToString(responseData.errors)
        return StringResponse(data, errors, httpResponse)
    }

    suspend fun stringSendMutation(
        url:String,
        mutation:Mutation,
        variables: JsonObject? = null,
        operationName: String = "",
        headers:List<HttpHeader> = listOf()
    ):StringResponse{
        val (body, httpResponse) = send(url, mutation, variables, operationName, headers)
        val responseData = Json.decodeFromString<InternalResponse<JsonObject>>(body)
        val data = responseData.data.toString()
        val errors = Json.encodeToString(responseData.errors)
        return StringResponse(data, errors, httpResponse)
    }

}