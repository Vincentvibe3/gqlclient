package io.github.vincentvibe3.gqlclient

fun mutation(
    name: String="",
    init: Mutation.() -> Unit
): Mutation {
    val mutation = Mutation(name)
    mutation.init()
    return mutation
}

data class Mutation(val name: String): QueryElement(name){

    fun variable(name: String, type: String){
        components.add(Variable("$$name", type))
    }

    fun fragment(
        name:String,
        type:String,
        init: Fragment.() -> Unit
    ): Fragment {
        val fragment = Fragment(name, type, false)
        components.add(fragment)
        fragment.init()
        return fragment
    }

    override fun toString(): String {
        return "mutation "+super.toString()
    }

}