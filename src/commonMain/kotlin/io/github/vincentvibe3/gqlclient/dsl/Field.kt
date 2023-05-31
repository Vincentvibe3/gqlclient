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
): QueryElement(name){

    var alias:String? = null

    /**
     * Types that arguments can be
     */
    enum class ArgumentType{
        STRING_LITERAL, TYPE, NUMBER, VARIABLE
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
     * [ArgumentType.VARIABLE] becomes `$value`. [ArgumentType.STRING_LITERAL] becomes `"value"`.
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
            ArgumentType.VARIABLE -> "$$typeName"
        }
        components.add(Argument(name, formattedTypeName))
    }

    /**
     * Adds an argument to the field
     *
     * @param name Name of the Argument
     * @param typeName Name of the type used by the argument.
     * @param type [ArgumentType] to determine formatting to be put on the `typeName`.
     * [ArgumentType.VARIABLE] becomes `$value`. [ArgumentType.STRING_LITERAL] becomes `"value"`.
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
            ArgumentType.VARIABLE -> "$$typeName"
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
        return fragment
    }

    /**
     * Adds an `include` directive to the field
     *
     * @param variable The variable used as a condition
     *
     * @see Directive
     */
    fun include(variable:String){
        components.add(Directive(Directive.DirectiveType.INCLUDE, variable))
    }

    /**
     * Adds an `skip` directive to the field
     *
     * @param variable The variable used as a condition
     *
     * @see Directive
     */
    fun skip(variable: String){
        components.add(Directive(Directive.DirectiveType.SKIP, variable))
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
        components.add(FragmentUse(fragment))
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