package io.github.vincentvibe3.gqlclient.http

import io.ktor.client.statement.*

/**
 * Deserialized response string received from a query or mutation.
 *
 * @param T The type to deserialize data into.
 * @param E The type to deserialize errors.
 * @property data The returned data if available. `null` if no data was received.
 * @property errors The returned errors if available. `null` if no data was received.
 * @property httpResponse The [HttpResponse] as returned by ktor.
 */

class Response<T, E:GQLError>(
    val data:T?=null,
    val errors:List<E>?=null,
    val httpResponse: HttpResponse
)