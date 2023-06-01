package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents a `__typename` from the GraphQL introspection API
 *
 * @see QueryElement.typename
 */
object TypenameIntrospection : Introspection {
    override fun toString(): String {
        return "__typename"
    }
}