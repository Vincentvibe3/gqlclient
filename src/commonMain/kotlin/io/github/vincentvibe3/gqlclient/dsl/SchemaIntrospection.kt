package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents a `__schema` from the GraphQL introspection API.
 * Allows getting information on the schema.
 *
 * @see Query.schema
 */
class SchemaIntrospection(override val parent: QueryElement?) : QueryElement("__schema", parent), Introspection