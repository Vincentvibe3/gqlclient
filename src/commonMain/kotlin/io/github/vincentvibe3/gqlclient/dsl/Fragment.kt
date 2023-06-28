package io.github.vincentvibe3.gqlclient.dsl

/**
 * Creates a [Fragment] that can be registered in a [Query] for use
 *
 * @param name Name of the fragment
 * @param type Type on which the fragment acts
 * @param init Lambda to set up the fragment
 *
 * @see Fragment
 * @see Field.useFragment
 * @see Query.registerFragment
 */
fun fragment(
    name:String,
    type:String,
    init: Fragment.() -> Unit
): Fragment {
    val fragment = Fragment(name, type, false)
    fragment.init()
    return fragment
}

/**
 * Represents a fragment
 *
 * Create fragments using [fragment], [Field.fragment] or [Query.fragment]
 *
 * @property name Name of the fragment
 * @property type Type that the fragment acts on
 * @property inline
 *
 * @see fragment
 * @see Field.useFragment
 * @see Query.registerFragment
 */
data class Fragment(
    val name:String,
    private val type:String,
    val inline:Boolean
): QueryElement("$name on $type", null) {

    /**
     * Generates the GraphQL string for the fragment
     */
    override fun toString(): String {
        return if (inline){
            super.toString()
        } else {
            "fragment ${super.toString()}"
        }
    }

}