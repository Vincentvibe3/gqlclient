package io.github.vincentvibe3.gqlclient.http

import io.ktor.client.statement.*

/**
 * Wraps the response and error received from a query or mutation as a string.
 *
 * @property data The returned data if available. `null` if no data was received.
 * @property errors The returned errors if available. `null` if no data was received.
 * @property httpResponse The [HttpResponse] as returned by ktor.
 */

class StringResponse(
    val data:String?=null,
    val errors:String?=null,
    val httpResponse: HttpResponse
)