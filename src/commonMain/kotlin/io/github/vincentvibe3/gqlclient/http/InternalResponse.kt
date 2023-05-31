package io.github.vincentvibe3.gqlclient.http

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
@PublishedApi
internal class InternalResponse<T:GQLError>(
    val data:JsonElement?=null,
    val errors:List<T>?=null
)
