package io.github.vincentvibe3.gqlclient.http

/**
 * Simple wrapper class representing a header entry for a HTTP request.
 *
 * @param name Name of the Header
 * @param value Value of the Header
 */
data class HttpHeader(
    val name:String,
    val value:String
)