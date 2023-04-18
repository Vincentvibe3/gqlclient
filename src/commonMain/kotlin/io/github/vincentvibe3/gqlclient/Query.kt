package io.github.vincentvibe3.gqlclient

fun query(
    name: String="",
    init: Query.() -> Unit
): Query {
    val query = Query(name)
    query.init()
    return query
}

data class Query(val name:String): QueryElement("query"){

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

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (!super.equals(other)) return false

        other as Query

        return name == other.name
    }
}