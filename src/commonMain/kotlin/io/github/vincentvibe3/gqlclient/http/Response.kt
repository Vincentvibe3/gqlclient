package io.github.vincentvibe3.gqlclient.http

import io.ktor.client.statement.*

@Suppress("unused")
class Response<T, K:GQLError>(
    val data:T?=null,
    val errors:List<K>?=null,
    val httpResponse: HttpResponse
)