package io.github.vincentvibe3.gqlclient


data class Field(
    val name:String,
): QueryElement(name){

    var alias:String? = null

    fun addArg(name:String, type: String){
        components.add(Argument(name, type))
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

    fun useFragment(name:String){
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