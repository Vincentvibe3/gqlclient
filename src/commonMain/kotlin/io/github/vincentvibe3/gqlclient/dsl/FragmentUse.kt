package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents a reference to a fragment in a field
 *
 * @property name Name of the fragment to use
 *
 * @see Field.useFragment
 */
data class FragmentUse(
    val name:Fragment,
    override val parent: QueryElement?
): QueryElement("...${name.name}", parent){

    /**
     * Generates the GraphQL string for the fragment usage
     */
    override fun toString(): String {
        return super.toString()
    }
}