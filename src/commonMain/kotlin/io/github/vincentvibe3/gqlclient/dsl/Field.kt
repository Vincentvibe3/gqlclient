package io.github.vincentvibe3.gqlclient.dsl


data class Field(
    val name:String,
): QueryElement(name){

    var alias:String? = null

    enum class ArgumentType{
        STRING_LITERAL, TYPE, NUMBER, VARIABLE
    }

    @Suppress("unused")
    fun addArg(name:String, typeName: String){
        components.add(Argument(name, typeName))
    }

    fun addArg(name:String, typeName: String, type:ArgumentType){
        val formattedTypeName = when(type){
            ArgumentType.NUMBER -> typeName
            ArgumentType.STRING_LITERAL -> "\"$typeName\""
            ArgumentType.TYPE -> typeName
            ArgumentType.VARIABLE -> "$$typeName"
        }
        components.add(Argument(name, formattedTypeName))
    }

    fun addArg(name:String, typeName: Long, type:ArgumentType){
        val formattedTypeName = when(type){
            ArgumentType.NUMBER -> "$typeName"
            ArgumentType.STRING_LITERAL -> "\"$typeName\""
            ArgumentType.TYPE -> "$typeName"
            ArgumentType.VARIABLE -> "$$typeName"
        }
        components.add(Argument(name, formattedTypeName))
    }

    @Suppress("unused")
    fun addArg(name:String, type: Long){
        components.add(Argument(name, "$type"))
    }

    fun fragment(
        type:String,
        init: Fragment.() -> Unit
    ): Fragment {
        val fragment = Fragment("...", type, true)
        components.add(fragment)
        fragment.init()
        return fragment
    }

    fun include(variable:String){
        components.add(Directive(Directive.DirectiveType.INCLUDE, variable))
    }

    fun skip(variable: String){
        components.add(Directive(Directive.DirectiveType.SKIP, variable))
    }

    fun useFragment(name:Fragment){
        components.add(FragmentUse(name))
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Field){
            other.hashCode()==this.hashCode()
        } else {
            super.equals(other)
        }
    }

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