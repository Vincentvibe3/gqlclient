package io.github.vincentvibe3.gqlclient.dsl

fun mutation(
    name: String="",
    init: Mutation.() -> Unit
): Mutation {
    val mutation = Mutation(name)
    mutation.init()
    return mutation
}

data class Mutation(val name: String): QueryElement(name), Operation{

    fun variable(name: String, type: String){
        components.add(Variable("$$name", type))
    }

    @Suppress("unused")

    fun fragment(
        name:String,
        type:String,
        init: Fragment.() -> Unit
    ): Fragment {
        val fragment = io.github.vincentvibe3.gqlclient.dsl.fragment(name, type, init)
        components.add(fragment)
        return fragment
    }

    override fun toString(): String {
        return "mutation "+super.toString()
    }

}