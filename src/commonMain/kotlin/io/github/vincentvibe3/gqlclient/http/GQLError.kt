package io.github.vincentvibe3.gqlclient.http

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Represents GraphQL errors that can be received.
 * Implement this class to deserialize custom errors.
 *
 * @property message The error message
 * @property locations Optional field representing the locations of the error in the query. Is null if missing
 * @property path Optional Path where the error occurred. Is null if missing
 * @property extensions Optional [JsonObject] holding the data on extensions that may be received
 *
 * @see DefaultGQLError
 */
interface GQLError {

    val message:String
    val locations:List<ErrorLocations>?
    val path:List<String>?
    val extensions:JsonObject?

    /**
     * Wrapper class for error locations
     *
     * @property line Line where the error occurred
     * @property column Column where the error occurred
     */
    @Serializable
    data class ErrorLocations(
        val line:Long,
        val column:Long
    )
}