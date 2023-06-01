package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents a `__type` from the GraphQL introspection API.
 *
 * @property type The type to get information on.
 *
 * @see Query.type
 */
data class TypeIntrospection(val type:String): QueryElement("__type"), Introspection {

    init {
        components.add(Argument("name", "\"$type\""))
    }

    /**
     * Generates the GraphQl string for the type introspection.
     */
    override fun toString(): String {
        return super.toString()
    }
}