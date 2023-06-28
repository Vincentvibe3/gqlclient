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
class Variable(
    name:String,
    val type:String
): QueryComponent {

    val name=name
        get() = "$$field"

    /**
     * Generate the GraphQl string for the variable declaration
     */
    override fun toString(): String {
        return "$name:$type"
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Variable){
            false
        } else {
            other.toString() == this.toString()
        }
    }
}