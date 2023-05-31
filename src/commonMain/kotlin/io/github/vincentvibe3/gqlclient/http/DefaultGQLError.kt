package io.github.vincentvibe3.gqlclient.http

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Default implementation of [GQLError]
 * Use when errors conform to the GraphQL spec
 * If other fields are specified consider implementing [GQLError]
 *
 *  @property message The error message
 *  @property locations Optional field representing the locations of the error in the query. Is null if missing
 *  @property path Optional Path where the error occurred. Is null if missing
 *  @property extensions Optional [JsonObject] holding the data on extensions that may be received
 *
 * @see GQLError
 */
@Serializable
class DefaultGQLError (
    override val message:String,
    override val locations:List<GQLError.ErrorLocations>?=null,
    override val path:List<String>?=null,
    override val extensions: JsonObject? = null
):GQLError