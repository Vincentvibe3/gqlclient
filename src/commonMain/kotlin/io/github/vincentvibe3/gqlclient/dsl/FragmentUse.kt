package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents a reference to a fragment in a field
 *
 * @property attachedFragment Name of the fragment to use
 *
 * @see Field.useFragment
 */
data class FragmentUse(
    val attachedFragment:Fragment,
    override val parent: QueryElement?
): QueryElement("...${attachedFragment.name}", parent){

    /**
     * Generates the GraphQL string for the fragment usage
     */
    override fun toString(): String {
        return super.toString()
    }
}