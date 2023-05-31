package io.github.vincentvibe3.gqlclient.http

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Class used to serialize the request payload for a mutation or query.
 *
 * @param query Query to send as a string
 * @param operationName Name of the operation to perform
 * @param variables Variables Json in string format
 *
 * @see GQLClient.sendMutation
 * @see GQLClient.sendQuery
 */
@Serializable
internal data class QueryRequest(
    val query:String,
    val operationName:String?=null,
    val variables:JsonObject? =null
)