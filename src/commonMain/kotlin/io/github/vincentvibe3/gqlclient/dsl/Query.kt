package io.github.vincentvibe3.gqlclient.dsl

/**
 * Creates a [Query]
 *
 * @param name Name for the query (Optional).
 * @param init Lambda to set up the [Query].
 *
 * @return Returns the created query
 *
 * @see Query
 */
fun query(
    name: String="",
    init: Query.() -> Unit
): Query {
    val query = Query(name)
    query.init()
    return query
}

/**
 * Represents a GraphQL query
 * Created using [query]
 *
 * @param name Name for the query (Optional).
 *
 * @see query
 */
data class Query(val name:String): QueryElement(name, null), Operation{

    /**
     * Puts type information into the query. Equivalent to adding a `__type`.
     *
     * @param type Name of the type to query information from.
     * @param init Lambda to set up the [TypeIntrospection].
     *
     * @return Returns the created [TypeIntrospection]
     *
     * @see TypeIntrospection
     */
    fun type(
        type:String,
        init: TypeIntrospection.() -> Unit
    ): TypeIntrospection {
        val typeIntrospection = TypeIntrospection(type, this)
        components.add(typeIntrospection)
        typeIntrospection.init()
        return typeIntrospection
    }

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
     * Create and register a fragment for use in the query
     *
     * @param name Name of the Fragment
     * @param type Name of the type that the fragment is to be applied on.
     * @param init Lambda to set up the [Fragment] to add.
     *
     * @return Returns the created [Fragment]
     *
     * @see Fragment
     */
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
     * Add schema info to a query. Equivalent to `__schema`.
     *
     * @param init lambda to set up the [SchemaIntrospection] to add.
     *
     * @return Returns the created [SchemaIntrospection]
     *
     * @see SchemaIntrospection
     */
    fun schema(
        init: SchemaIntrospection.() -> Unit = {}
    ): SchemaIntrospection {
        val schema = SchemaIntrospection(this)
        schema.init()
        components.add(schema)
        return schema
    }

    /**
     * Register an existing [Fragment] into the query
     *
     * @param fragment The [Fragment] to add
     *
     * @see fragment
     */
    fun registerFragment(fragment:Fragment){
        for (variable in fragment.usedVariables){
            registerVariable(variable)
        }
        if (!components.contains(fragment)) {
            components.add(fragment)
        }
    }

    /**
     * Converts the query object into a string containing a GraphQL query.
     *
     * @return the query as a string
     */
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