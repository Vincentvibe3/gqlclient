package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents a field to be queried
 *
 * @property name Name of the field to be queried
 * @property alias Alias for the field name
 *
 */
data class Field(
    val name:String,
    override val parent:QueryElement?
): QueryElement(name, parent){

    var alias:String? = null

    /**
     * Types that arguments can be
     */
    enum class ArgumentType{
        STRING_LITERAL, TYPE, NUMBER
    }

    /**
     * Adds an argument to the field
     *
     * @param name Name of the Argument
     * @param typeName Name of the type used by the argument. Note: The string is passed as is.
     *
     * @see Argument
     */
    @Suppress("unused")
    fun addArg(name:String, typeName: String){
        components.add(Argument(name, typeName))
    }

    /**
     * Adds an argument to the field
     *
     * @param name Name of the Argument
     * @param typeName Name of the type used by the argument.
     * @param type [ArgumentType] to determine formatting to be put on the `typeName`.
     * [ArgumentType.STRING_LITERAL] becomes `"value"`.
     * [ArgumentType.NUMBER] and [ArgumentType.TYPE] are passed as is
     *
     * @see Argument
     * @see ArgumentType
     */
    fun addArg(name:String, typeName: String, type:ArgumentType){
        val formattedTypeName = when(type){
            ArgumentType.NUMBER -> typeName
            ArgumentType.STRING_LITERAL -> "\"$typeName\""
            ArgumentType.TYPE -> typeName
        }
        components.add(Argument(name, formattedTypeName))
    }

    /**
     * Adds a variable as an argument to the field.
     * This will automatically register the variable for use in the query or mutation.
     *
     * @param name Name of the Argument
     * @param variable Variable to use.
     *
     */
    fun addArg(name: String, variable: Variable){
        registerVariable(variable)
        components.add(Argument(name, variable.name))
    }

    /**
     * Adds an argument to the field
     *
     * @param name Name of the Argument
     * @param typeName Name of the type used by the argument.
     * @param type [ArgumentType] to determine formatting to be put on the `typeName`.
     * [ArgumentType.STRING_LITERAL] becomes `"value"`.
     * [ArgumentType.NUMBER] and [ArgumentType.TYPE] are passed as is
     *
     * @see Argument
     * @see ArgumentType
     */
    fun addArg(name:String, typeName: Long, type:ArgumentType){
        val formattedTypeName = when(type){
            ArgumentType.NUMBER -> "$typeName"
            ArgumentType.STRING_LITERAL -> "\"$typeName\""
            ArgumentType.TYPE -> "$typeName"
        }
        components.add(Argument(name, formattedTypeName))
    }

    /**
     * Adds an argument to the field
     *
     * @param name Name of the Argument
     * @param value Name of the type used by the argument. Note: The string is passed as is.
     *
     * @see Argument
     */
    @Suppress("unused")
    fun addArg(name:String, value: Long){
        components.add(Argument(name, "$value"))
    }

    /**
     * Create and register an inline fragment for use in the field
     *
     * @param type Name of the type that the fragment is to be applied on.
     * @param init Lambda to set up the [Fragment] to add.
     *
     * @return Returns the created [Fragment]
     *
     * @see Fragment
     */
    fun fragment(
        type:String,
        init: Fragment.() -> Unit
    ): Fragment {
        val fragment = Fragment("...", type, true)
        components.add(fragment)
        fragment.init()
        for (variable in fragment.usedVariables){
            registerVariable(variable)
        }
        return fragment
    }

    /**
     * Adds an `include` directive to the field
     *
     * @param bool Boolean controlling the directive
     *
     * @see Directive
     */
    fun include(bool:Boolean){
        components.add(Directive(Directive.DirectiveType.INCLUDE, bool.toString()))
    }

    fun include(variable: Variable){
        registerVariable(variable)
        components.add(Directive(Directive.DirectiveType.INCLUDE, variable.name))
    }

    /**
     * Adds an `skip` directive to the field
     *
     * @param bool Boolean controlling the directive
     *
     * @see Directive
     */
    fun skip(bool: Boolean){
        components.add(Directive(Directive.DirectiveType.SKIP, bool.toString()))
    }

    fun skip(variable: Variable){
        registerVariable(variable)
        components.add(Directive(Directive.DirectiveType.SKIP, variable.name))
    }

    /**
     * Inserts a fragment into the field
     *
     * @param fragment [Fragment] to be added
     *
     * @see io.github.vincentvibe3.gqlclient.dsl.fragment
     * @see Fragment
     */
    fun useFragment(fragment:Fragment){
        registerFragmentToParent(fragment)
        for (variable in fragment.usedVariables){
            registerVariable(variable)
        }
        components.add(FragmentUse(fragment, this))
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Field){
            other.hashCode()==this.hashCode()
        } else {
            super.equals(other)
        }
    }

    /**
     * Generates the GraphQL string for the field
     */
    override fun toString(): String {
        return if (alias.isNullOrBlank()){
            super.toString()
        } else {
            "$alias:${super.toString()}"
        }
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (alias?.hashCode() ?: 0)
        return result
    }

}