package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents a reference to a fragment in a field
 *
 * @property name Name of the fragment to use
 *
 * @see Field.useFragment
 */
data class FragmentUse(
    val name:Fragment
): QueryElement("...${name.name}"){

    /**
     * Generates the GraphQL string for the fragment usage
     */
    override fun toString(): String {
        return super.toString()
    }
}