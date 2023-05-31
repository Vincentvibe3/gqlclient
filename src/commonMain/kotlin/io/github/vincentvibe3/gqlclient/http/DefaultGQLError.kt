package io.github.vincentvibe3.gqlclient.http

import kotlinx.serialization.Serializable

@Serializable
class DefaultGQLError (
    override val message:String,
    override val locations:List<GQLError.ErrorLocations>?=null,
    override val path:List<String>?=null,
):GQLError