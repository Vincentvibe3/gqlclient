package io.github.vincentvibe3.gqlclient.dsl

fun query(
    name: String="",
    init: Query.() -> Unit
): Query {
    val query = Query(name)
    query.init()
    return query
}

data class Query(val name:String): QueryElement(name){

    fun type(
        type:String,
        init: TypeIntrospection.() -> Unit
    ): TypeIntrospection {
        val typeIntrospection = TypeIntrospection(type)
        components.add(typeIntrospection)
        typeIntrospection.init()
        return typeIntrospection
    }

    fun variable(name: String, type: String){
        components.add(Variable("$$name", type))
    }

    fun fragment(
        name:String,
        type:String,
        init: Fragment.() -> Unit
    ): Fragment {
        val fragment = io.github.vincentvibe3.gqlclient.dsl.fragment(name, type, init)
        components.add(fragment)
        return fragment
    }

    fun schema(
        init: SchemaIntrospection.() -> Unit = {}
    ): SchemaIntrospection {
        val schema = SchemaIntrospection()
        schema.init()
        components.add(schema)
        return schema
    }

    fun registerFragment(fragment:Fragment){
        components.add(fragment)
    }

    override fun toString(): String {
        return "query "+super.toString()
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