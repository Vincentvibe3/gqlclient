package io.github.vincentvibe3.gqlclient.http

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Class used to deserialize the json received from a request
 * Contents will be transferred into a [Response]
 *
 * @see Response
 */
@Serializable
@PublishedApi
internal class InternalResponse<T>(
    val data:JsonElement?=null,
    val errors:List<T>?=null
)
