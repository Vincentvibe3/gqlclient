package io.github.vincentvibe3.gqlclient.dsl

/**
 * Base class of all components of a GraphQl query
 */
interface QueryComponent {

    /**
     * Generates the GraphQL string for the component
     */
    override fun toString():String

}