package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents a component in a query that has children
 *
 * @property queryElementName Name of the component to add
 */
sealed class QueryElement(private val queryElementName: String): QueryComponent {

    protected val components = ArrayList<QueryComponent>()

    /**
     * Gets the name of the current type of the current [QueryElement]. Equivalent to `__typename`.
     */
    fun typename(){
        components.add(TypenameIntrospection)
    }

    /**
     * Creates and register a [Field] as a child of the current [QueryElement]
     *
     * @return returns the created [Field]
     */
    fun field(
        name:String,
        init: Field.() -> Unit = {}
    ): Field {
        val field = Field(name)
        field.init()
        components.add(field)
        return field
    }

    override fun equals(other: Any?): Boolean {
        return if (other is QueryElement){
            other.hashCode()==this.hashCode()
        } else {
            super.equals(other)
        }
    }

    /**
     * Generates the GraphQL string for the current element.
     */
    override fun toString(): String {
        var queryString = queryElementName
        if (components.isNotEmpty()){
            val arguments = components.filterIsInstance<Argument>().joinToString(",")
            if (arguments.isNotBlank()){
                queryString+="($arguments)"
            }
            val variables = components.filterIsInstance<Variable>().joinToString(",")
            if (variables.isNotBlank()){
                queryString+="($variables)"
            }
            queryString+=components.filterIsInstance<Directive>().joinToString(",")

            val otherComponents = components.filter {
                it is Field || it is FragmentUse || it is Introspection || (it is Fragment && it.inline)
            }
            if (otherComponents.isNotEmpty()){
                queryString+="{"
                queryString+=otherComponents.joinToString(",")
                queryString+="}"
            }
        }
        val fragmentDeclarations = components.filterIsInstance<Fragment>().filter {
            !it.inline
        }.joinToString(",")
        if (fragmentDeclarations.isNotBlank()){
            queryString+=",$fragmentDeclarations"
        }
        return queryString
    }

    override fun hashCode(): Int {
        var result = queryElementName.hashCode()
        result = 31 * result + components.hashCode()
        return result
    }

}