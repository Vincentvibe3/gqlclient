package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents a variable declaration in a query.
 *
 * @property name The variable name.
 * @property type The type of the variable.
 *
 * @see Query.variable
 * @see Mutation.variable
 */
data class Variable(
    private val name:String,
    private val type:String
): QueryComponent {

    /**
     * Generate the GraphQl string for the variable declaration
     */
    override fun toString(): String {
        return "$name:$type"
    }
}