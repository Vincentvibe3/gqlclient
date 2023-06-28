package io.github.vincentvibe3.gqlclient.dsl

/**
 * Creates a [Mutation]
 *
 * @param name Name for the mutation (Optional).
 * @param init Lambda to set up the [Mutation].
 *
 * @return Returns the created [Mutation]
 *
 * @see Mutation
 */
fun mutation(
    name: String="",
    init: Mutation.() -> Unit
): Mutation {
    val mutation = Mutation(name)
    mutation.init()
    return mutation
}

/**
 * Represents a GraphQL mutation
 * Created using [query]
 *
 * @param name Name for the mutation (Optional).
 *
 * @see mutation
 */
data class Mutation(val name: String): QueryElement(name, null), Operation{

    /**
     * Create a variable for use in the GraphQL query
     *
     * @param name Name of the variable. Do not put the $, It will be added
     * @param type Name of the type of the variable.
     *
     */
    fun variable(name: String, type: String){
        components.add(Variable(name, type))
    }

    fun variable(variable: Variable){
        components.add(variable)
    }

    /**
     * Create and register a fragment for use in the mutation
     *
     * @param name Name of the Fragment
     * @param type Name of the type that the fragment is to be applied on.
     * @param init Lambda to set up the [Fragment] to add.
     *
     * @return Returns the created [Fragment]
     *
     * @see Fragment
     */
    @Suppress("unused")
    fun fragment(
        name:String,
        type:String,
        init: Fragment.() -> Unit
    ): Fragment {
        val fragment = io.github.vincentvibe3.gqlclient.dsl.fragment(name, type, init)
        components.add(fragment)
        for (variable in fragment.usedVariables){
            registerVariable(variable)
        }
        return fragment
    }

    /**
     * Register an existing [Fragment] into the mutation
     *
     * @param fragment The [Fragment] to add
     *
     * @see fragment
     */
    fun registerFragment(fragment:Fragment){
        for (variable in fragment.usedVariables){
            registerVariable(variable)
        }
        components.add(fragment)
    }

    /**
     * Converts the query object into a string containing a GraphQL query.
     *
     * @return the query as a string
     */
    override fun toString(): String {
        return "mutation "+super.toString()
    }

}