package io.github.vincentvibe3.gqlclient.http

import kotlinx.serialization.Serializable

interface GQLError {

    val message:String
    val locations:List<ErrorLocations>?
    val path:List<String>?

    @Serializable
    data class ErrorLocations(
        val line:Long,
        val column:Long
    )
}