package io.github.vincentvibe3.gqlclient.http

import kotlinx.serialization.Serializable

@Serializable
data class QueryRequest(
    val query:String,
    val operationName:String?=null,
    val variables:String? =null
)