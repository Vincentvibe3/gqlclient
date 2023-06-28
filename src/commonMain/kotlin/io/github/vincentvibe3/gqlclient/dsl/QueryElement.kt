package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents a component in a query that has children
 *
 * @property queryElementName Name of the component to add
 */
sealed class QueryElement(private val queryElementName: String, open val parent:QueryElement?): QueryComponent {

    internal val components = ArrayList<QueryComponent>()

    internal fun registerVariable(variable: Variable){
        if (this is Query){
            if(!this.components.contains(variable)){
                this.variable(variable)
            }
        } else if (this is Mutation) {
            if(!this.components.contains(variable)){
                this.variable(variable)
            }
        } else {
            var nextParent = parent
            while (nextParent?.parent != null){
                nextParent = nextParent.parent
            }
            when (nextParent){
                is Query -> {
                    if (!nextParent.components.contains(variable)) {
                        nextParent.variable(variable)
                    }
                }
                is Mutation -> {
                    if (!nextParent.components.contains(variable)) {
                        nextParent.variable(variable)
                    }
                }
                is Fragment -> {
                    if (!nextParent.components.contains(variable)) {
                        nextParent.variable(variable)
                    }
                }
                else -> { throw IllegalStateException("Root must be Query, Mutation or Fragment") }
            }
        }

    }

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
        val field = Field(name, this)
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