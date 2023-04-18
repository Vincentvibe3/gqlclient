package io.github.vincentvibe3.gqlclient

fun mutation(
    name: String="",
    init: Mutation.() -> Unit
): Mutation {
    val mutation = Mutation(name)
    mutation.init()
    return mutation
}

data class Mutation(val name: String): QueryElement("mutation"){

    fun variable(name: String, type: String){
        variables.add(Pair("$$name", type))
    }

    fun fragment(
        name:String,
        type:String,
        init: Fragment.() -> Unit
    ): Fragment {
        val fragment = Fragment(name, type, false)
        fragments.add(fragment)
        fragment.init()
        return fragment
    }

    override fun toString(): String {
        var queryString = "$queryElementName $name"
        val varStrings = variables.joinToString(separator = ",") { "${it.first}:${it.second}" }
        if (variables.isNotEmpty()){
            queryString+="($varStrings)"
        }
        queryString+="{"
        queryString+=fields.joinToString(separator=",") {
            it.toString()
        }
        queryString+="}"
        if (fragments.isNotEmpty()){
            queryString+=","
        }
        queryString+=fragments.joinToString(separator = ",") { it.toString() }
        return queryString
    }
}